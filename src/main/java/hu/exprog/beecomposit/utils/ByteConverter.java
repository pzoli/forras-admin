package hu.exprog.beecomposit.utils;
import java.io.DataInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class ByteConverter {
	private Class<?> clazz;
	private Method getter;

	public ByteConverter(Class<?> clazz, Integer byteSize, String converterMethodName) throws NoSuchMethodException, SecurityException {
		super();
		this.clazz = clazz;
		Method method = DataInputStream.class.getDeclaredMethod(converterMethodName);
		getter = method;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object getValueOf(DataInputStream dis) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object result = getter.invoke(dis, new Object[] {});
		if (clazz.equals(Character.class) && result instanceof Integer) {
			result = (char)(((Integer)result).intValue()+'0');
		} else if (clazz.equals(Character.class) && result instanceof Short) {
			result = (char)(((Integer)result).shortValue()+'0');
		} else if (clazz.equals(Character.class) && result instanceof Byte) {
			result = (char)(((Integer)result).byteValue()+'0');
		} else if (clazz.equals(Short.class) && result instanceof Integer) {
			result = ((Integer)result).shortValue();
		} else {
			result = clazz.cast(result);
		}
		return result;
	}
}
