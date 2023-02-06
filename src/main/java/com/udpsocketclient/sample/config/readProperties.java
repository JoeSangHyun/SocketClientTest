package com.udpsocketclient.sample.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class readProperties {

    /**
     * prop.property("key"); 로 값을 가져올 수 있도록 Properties를 일거
     * Properties를 반환한다.
     * 기본 경로 src/main/resource/
     *
     * @param propFileName : 파일명 (경로 : 파일명)
     * @throw FileNotFoundException
     * @author shjoe133
     */
    public Properties readProperties(String propFileName) {
        Properties prop = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        try {
            if(inputStream != null) {
                prop.load(inputStream);
                return prop;
            } else {
                throw new FileNotFoundException("프로퍼티 파일 '" + propFileName + "'을 resource 에서 찾을 수 없습니다." );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
