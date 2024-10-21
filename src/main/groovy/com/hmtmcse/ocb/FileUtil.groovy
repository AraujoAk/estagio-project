package com.hmtmcse.ocb

import grails.util.Holders
import org.springframework.web.multipart.MultipartFile

class FileUtil {

    public static String getRootPath() {
        return Holders.servletContext?.getRealPath("")
    }

    public static File makeDirectory(String path) {
        File file = new File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    public static String uploadContactImage(Integer contactId, MultipartFile multipartFile) {
        if (contactId && multipartFile) {
            String contactImagePath = "${getRootPath()}contact-image/"
            makeDirectory(contactImagePath)
            multipartFile.transferTo(new File(contactImagePath, contactId + "-" + multipartFile.originalFilename))
            return multipartFile.originalFilename
        }
        return ""
    }

    public static void deleteContactImage(String imageName) {
        String contactImagePath = "${getRootPath()}contact-image/"
        File imageFile = new File(contactImagePath, imageName)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }
}
