package com.tfit.BdBiProcSrvShEduOmc.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import io.netty.buffer.ByteBufUtil;

public final class UUIDUtil {
    public static byte[] convertUUIDToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        //NumberUtil.serialize(uuid.getMostSignificantBits(), bytes, 0);
        //NumberUtil.serialize(uuid.getLeastSignificantBits(), bytes, 8);
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		for (int i = 7; i >= 0; --i) {
			bytes[i] = (byte) (msb & 0xff);
			msb >>= 8;
		}
		for (int i = 15; i >= 8; --i) {
			bytes[i] = (byte) (lsb & 0xff);
			lsb >>= 8;
		}
        return bytes;
    }

    public static UUID convertBytesToUUID(byte[] bytes) {
        long msb = 0;
        long lsb = 0;
        assert bytes.length == 16;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (bytes[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        //return new UUID(NumberUtil.unSerializeToLong(bytes, 0), NumberUtil.unSerializeToLong(bytes, 8));
        return new UUID(msb, lsb);
    }
    
    //MD5算法函数
    public static String getMD5(String value, boolean isUpper) {
        MessageDigest md;
        String strMd5Result = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] md5ByteArr = md.digest(value.getBytes());
            if(isUpper)
            	strMd5Result = ByteBufUtil.hexDump(md5ByteArr).toUpperCase();
            else
            	strMd5Result = ByteBufUtil.hexDump(md5ByteArr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return strMd5Result;
    }
}
