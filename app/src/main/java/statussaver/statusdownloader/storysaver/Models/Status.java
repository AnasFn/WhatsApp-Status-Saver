package statussaver.statusdownloader.storysaver.Models;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.Objects;

public class Status {
    private File file;
    private String title;
    private String path;
    private boolean isVideo;
    private boolean isApi30;
    private DocumentFile documentFile;

    public Status(File file, String title, String path) {
        this.file = file;
        this.title = title;
        this.path = path;
        this.isVideo = isFileExtensionMP4(file);
        this.isApi30 = false;
    }

    public Status(DocumentFile documentFile) {
        this.documentFile = documentFile;
        this.isVideo = isFileExtensionMP4(documentFile);
        this.isApi30 = true;
    }

    public DocumentFile getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(DocumentFile documentFile) {
        this.documentFile = documentFile;
    }

    public boolean isApi30() {
        return isApi30;
    }

    public void setApi30(boolean api30) {
        this.isApi30 = api30;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    private boolean isFileExtensionMP4(File file) {
        String extension = getFileExtension(file);
        return extension != null && extension.equalsIgnoreCase("mp4");
    }

    private boolean isFileExtensionMP4(DocumentFile documentFile) {
        String extension = getFileExtension(documentFile.getName());
        return extension != null && extension.equalsIgnoreCase("mp4");
    }

    private String getFileExtension(File file) {
        if (file != null) {
            String name = file.getName();
            int lastDotIndex = name.lastIndexOf('.');
            if (lastDotIndex >= 0 && lastDotIndex < name.length() - 1) {
                return name.substring(lastDotIndex + 1).toLowerCase();
            }
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null) {
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex >= 0 && lastDotIndex < fileName.length() - 1) {
                return fileName.substring(lastDotIndex + 1).toLowerCase();
            }
        }
        return null;
    }
}
