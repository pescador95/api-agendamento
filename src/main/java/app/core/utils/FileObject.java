package app.core.utils;


public class FileObject {
    private String objectKey;

    private Long size;

    public FileObject() {
    }

    public String getObjectKey() {
        return objectKey;
    }

    public Long getSize() {
        return size;
    }

    public FileObject setObjectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public FileObject setSize(Long size) {
        this.size = size;
        return this;
    }
}