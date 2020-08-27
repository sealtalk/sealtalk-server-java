package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;

public class N3d {

    private static final String N3D_KEY = "11EdDIaqpcim";

    private static final N3d n3d = new N3d(1, 4294967295L);

    private long keyCode = 0;
    private String key;
    private int radix;
    private long lower;
    private long upper;
    private char[][] dict = new char[62][62];

    private N3d(long lower, long upper) {
        SealtalkConfig sealtalkConfig = SpringContextUtil.getBean(SealtalkConfig.class);
        this.key = sealtalkConfig.getN3dKey();
        this.lower = lower;
        this.upper = upper;
        char[] charMap = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        this.radix = dict[0].length;

        if (this.upper <= this.lower) {
            throw new RuntimeException("Parameter is error.");
        }

        if (key == null || key.length() == 0)
            throw new RuntimeException("The key is error.");

        for (int i = 0, n = 0, ref = key.length(); 0 <= ref ? n < ref : n > ref; i = 0 <= ref ? ++n : --n) {
            char a = key.charAt(i);
            if (a > 127) {
                throw new RuntimeException("The key is error.");
            }
            this.keyCode += a * Math.pow(128, i % 7);
        }

        if (this.keyCode + this.radix < this.upper) {
            throw new RuntimeException("The secret key is too short.");
        }
        long i = this.keyCode - this.radix;
        int j = 0;
        while (i < this.keyCode) {
            int k = this.radix;
            int l = 0;
            while (k > 0) {
                int s = (int) (i % k);
                this.dict[j][l] = charMap[s];

                charMap[s] = charMap[k - 1];
                k--;
                l++;
            }
            for (int x = 0; x < charMap.length; x++) {
                charMap[x] = this.dict[j][x];
            }
            i++;
            j++;
        }
    }


    private String encrypt(long num) {
        if (num > this.upper || num < this.lower) {
            throw new RuntimeException("Parameter is error.");
        }

        num = this.keyCode - num;
        StringBuilder result = new StringBuilder();
        int m = (int) (num % this.radix);
        char[] map = this.dict[m];

        int s = 0;
        result.append(this.dict[0][m]);
        while (num > this.radix) {
            num = (num - m) / this.radix;
            m = (int) (num % this.radix);
            if ((s = m + s) >= this.radix) {
                s -= this.radix;
            }
            result.append(map[s]);
        }
        return result.toString();
    }

    private long decrypt(String str) {
        long result = 0;
        if (str == null || str.length() == 0) {
            throw new RuntimeException("Parameter is error.");
        }
        char[] chars = str.toCharArray();
        int len = chars.length;
        int t = 0;
        int s = 0;
        result = new String(this.dict[0]).indexOf(chars[0]);
        if (result < 0)
            throw new RuntimeException("Invalid string.");
        String map = new String(this.dict[(int) result]);

        for (int i = 1, n = 1, ref = len; 1 <= ref ? n < ref : n > ref; i = 1 <= ref ? ++n : --n) {

            int j = map.indexOf(chars[i]);

            if (j < 0) {
                throw new RuntimeException("Invalid string.");
            }
            if ((s = j - t) < 0) {
                s += this.radix;
            }
            result += s * Math.pow(this.radix, i);
            t = j;
        }
        result = this.keyCode - result;
        return result;
    }

    public static String encode(long num) throws ServiceException {
        try {
            return n3d.encrypt(num);
        }catch (Exception e){
            throw new ServiceException(ErrorCode.REQUEST_ERROR,"id format error");
        }
    }

    public static int decode(String str) throws ServiceException {
        try {
            Long id= n3d.decrypt(str);
            return id.intValue();
        }catch (Exception e){
            throw new ServiceException(ErrorCode.REQUEST_ERROR,"id format error");
        }

    }

    public static void main(String[] args) {
        N3d n = new N3d(1, 4294967295L);
        for (long i = 1; i < 4294967295L; i++) {
            String ret = n.encrypt(i);
            System.out.println(ret);
            System.out.println(n.decrypt(ret));
        }
    }
}
