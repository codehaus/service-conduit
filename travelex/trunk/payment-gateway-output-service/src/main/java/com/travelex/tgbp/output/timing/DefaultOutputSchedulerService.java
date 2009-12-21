package com.travelex.tgbp.output.timing;

import java.util.concurrent.TimeUnit;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.sca4j.api.annotation.Resource;
import org.sca4j.timer.spi.TimerService;

import com.travelex.tgbp.output.service.FileOutputService;
import com.travelex.tgbp.output.service.InstructionOutputService;

/**
 * Default implementation for {@link OutputSchedulerService}
 */
@Scope("COMPOSITE")
@EagerInit
public class DefaultOutputSchedulerService implements OutputSchedulerService {

    @Reference protected InstructionOutputService instructionOutputService;
    @Reference protected FileOutputService fileOutputService;

    @Resource(mappedName = "TransactionalTimerService") protected TimerService trxTimerService;

    @Property(required = true) protected long initialStartupDelayInSeconds;
    @Property(required = true) protected long instructionOutputDelayInSeconds;
    @Property(required = true) protected long fileOutputDelayInSeconds;

    /**
     * Prepares instruction and file output process jobs and schedules these jobs using quartz timer API.
     */
    @Init
    public void init(){

       trxTimerService.scheduleWithFixedDelay(getIntructionOutputJob()
               , TimeUnit.MILLISECONDS.convert(initialStartupDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(instructionOutputDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS);

       trxTimerService.scheduleWithFixedDelay(getFileOutputJob()
               , TimeUnit.MILLISECONDS.convert(initialStartupDelayInSeconds + instructionOutputDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(fileOutputDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS);
    }

    /*
     * Creates instruction output process job
     */
    private Runnable getIntructionOutputJob() {
       return new Runnable() {
         @Override
         public void run() {
           instructionOutputService.outputInstructions(null);
         }
       };
    }

    /*
     * Creates file output process job
     */
    private Runnable getFileOutputJob() {
       return new Runnable() {
         @Override
         public void run() {
             fileOutputService.outputFiles(null);
         }
       };
    }

}
