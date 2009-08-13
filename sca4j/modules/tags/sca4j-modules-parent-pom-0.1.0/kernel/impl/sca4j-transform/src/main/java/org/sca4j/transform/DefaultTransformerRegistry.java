/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.transform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sca4j.scdl.DataType;
import org.sca4j.transform.Transformer;
import org.sca4j.transform.TransformerRegistry;

/**
 * @version $Rev: 3524 $ $Date: 2008-03-31 22:43:51 +0100 (Mon, 31 Mar 2008) $
 */
public class DefaultTransformerRegistry<T extends Transformer> implements TransformerRegistry<T> {
    private final Map<TransformerPair, T> transformers = new ConcurrentHashMap<TransformerPair, T>();

    
    public void register(T transformer) {
        TransformerPair pair = new TransformerPair(transformer.getSourceType(), transformer.getTargetType());
        transformers.put(pair, transformer);
    }

    public void unregister(T transformer) {
        TransformerPair pair = new TransformerPair(transformer.getSourceType(), transformer.getTargetType());
        transformers.remove(pair);
    }

    public T getTransformer(DataType<?> source, DataType<?> target) {
        TransformerPair pair = new TransformerPair(source, target);
        return transformers.get(pair);
    }

    private static class TransformerPair {
        private final DataType<?> source;
        private final DataType<?> target;


        public TransformerPair(DataType<?> source, DataType<?> target) {
            this.source = source;
            this.target = target;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TransformerPair that = (TransformerPair) o;

            return source.equals(that.source) && target.equals(that.target);

        }

        public int hashCode() {
            int result;
            result = source.hashCode();
            result = 31 * result + target.hashCode();
            return result;
        }
    }
}
