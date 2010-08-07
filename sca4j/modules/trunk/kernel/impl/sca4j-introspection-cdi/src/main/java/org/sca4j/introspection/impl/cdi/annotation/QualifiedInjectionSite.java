package org.sca4j.introspection.impl.cdi.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Qualifier;

/**
 * Inspects and stores qualifier metadata for a single injection site.
 * @see Qualifier
 */
public class QualifiedInjectionSite {

	//These are (usually) implicit qualifiers which, when present, add no useful wiring data.
	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> IGNORABLE_QUALIFIERS = Arrays.asList(Any.class, Default.class);	
	
	private List<Annotation> annotations;

	/**
	 * Constructor.
	 * @param field a {@link Field} injection site.
	 */
	public QualifiedInjectionSite(Field field) {
		annotations = getQualifiers(field.getAnnotations());
	}

	/**
	 * Constructor.
	 * @param method a {@link Method} injection site.
	 * @param parameterIndex the index of the parameter which is the injection site.
	 */
	public QualifiedInjectionSite(Method method, int parameterIndex) {
		annotations = getQualifiers(method.getParameterAnnotations()[parameterIndex]);
	}

	/**
	 * Constructor.
	 * @param method a {@link Constructor} injection site.
	 * @param parameterIndex the index of the parameter which is the injection site.
	 */	
	public QualifiedInjectionSite(Constructor<?> constructor, int index) {
		annotations = getQualifiers(constructor.getParameterAnnotations()[index]);
	}
	
	public void resolve() {
		//TODO: maybe
	}

	/**
	 * Gets all of the qualifiers associated with this injection site.
	 * @return the list of qualifiers in the declared order.
	 */
	public List<Annotation> getQualifiers() {
		return annotations;
	}

	/**
	 * Were any meaningful qualifiers found. Qualifiers such as {@link Any} and {@link Default} are not
	 * regarded as meaningful.
	 * @return true if a meaningful qualifier was found. False otherwise.
	 */
	boolean isQualified() {
		return getQualifiers().size() > 0;
	}

	/**
	 * Currently only used by tests. Might remove.
	 * @param <T> the qualifier annotation type.
	 * @param clazz the qualifier annotation class.
	 * @param index the declaration order index of the qualifier.
	 * @return the instance of the annotation type.
	 */
	<T> T getQualifier(Class<T> clazz, int index) {
		return clazz.cast(annotations.get(index));
	}
	
	/**
	 * Extract the list of meaningful qualifiers from the injection site's annotation set.
	 * @param annotations the list of qualifiers on the injection site.
	 * @return the list of qualifiers on the injection site or an empty list.
	 */
	private List<Annotation> getQualifiers(Annotation[] annotations) { 
		List<Annotation> result = new ArrayList<Annotation>();
		for (Annotation annotation : annotations) {    		  
			if (!IGNORABLE_QUALIFIERS.contains(annotation.annotationType())) {
	    		boolean isQualifier = annotation.annotationType().getAnnotation(Qualifier.class) != null;
	    		if (isQualifier) {
	    			result.add(annotation);
	    		}				
			}
		}		
		
		return result;
	}		
	
}
