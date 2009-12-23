package com.travelex.tgbp.output.timing;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Resource;
import org.sca4j.timer.spi.TimerService;

/**
 * Default implementation for {@link OutputSchedulerService}
 */
public class DefaultOutputSchedulerService implements OutputSchedulerService {

    @Reference protected OutputServiceWrapper outputServiceWrapper;

    @Resource(mappedName = "TransactionalTimerService") protected TimerService trxTimerService;

    @Property(required = true) protected long initialStartupDelayInSeconds;
    @Property(required = true) protected long instructionOutputDelayInSeconds;
    @Property(required = true) protected long fileOutputDelayInSeconds;



    /**
     * {@inheritDoc}
     */
    public String startOutputService() {
        outputServiceWrapper.outputInstructions(null);
        outputServiceWrapper.outputFiles(null);
        return "Success";
    }

     /*private void performOutput()  {

          if (isServiceUp.get()) {

         return "Service already running, can't be restarted, restart app server instead";

     } else {

         trxTimerService.scheduleWithFixedDelay(getIntructionOutputJob()
                 , TimeUnit.MILLISECONDS.convert(initialStartupDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(instructionOutputDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS);

         trxTimerService.scheduleWithFixedDelay(getFileOutputJob()
                 , TimeUnit.MILLISECONDS.convert(initialStartupDelayInSeconds + instructionOutputDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(fileOutputDelayInSeconds, TimeUnit.SECONDS), TimeUnit.MILLISECONDS);

         isServiceUp.set(true);

         return "Service started successfully";
     }


         outputServiceWrapper.outputInstructions(null);
         outputServiceWrapper.outputFiles(null);

     }*/
}
