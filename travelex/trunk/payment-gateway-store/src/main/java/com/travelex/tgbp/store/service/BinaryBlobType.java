package com.travelex.tgbp.store.service;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


public class BinaryBlobType implements UserType {



    public int[] sqlTypes() {
        return new int[] { Types.BLOB };
    }

    public Class<byte[]> returnedClass() {
        return byte[].class;
    }

    public boolean equals(Object x, Object y) {
        return (x == y) || (x != null && y != null && java.util.Arrays.equals((byte[]) x, (byte[]) y));
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        final Blob blob = rs.getBlob(names[0]);
        final byte[] data =  blob == null ? "".getBytes() :  blob.getBytes(1, (int) blob.length());
        return data;
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        st.setBlob(index, Hibernate.createBlob((byte[]) value));
    }

    public Object deepCopy(Object value) {
        if (value == null)
            return null;

        byte[] bytes = (byte[]) value;
        byte[] result = new byte[bytes.length];
        System.arraycopy(bytes, 0, result, 0, bytes.length);

        return result;
    }

    public boolean isMutable() {
        return false;
    }

    @Override
    public Object assemble(Serializable cached, Object object) throws HibernateException {
        return cached;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (byte[])value;
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }


}
