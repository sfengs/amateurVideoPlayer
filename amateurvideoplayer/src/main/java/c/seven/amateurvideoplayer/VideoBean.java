package c.seven.amateurvideoplayer;

import java.io.Serializable;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class VideoBean implements Serializable {
    private String id;
    private String playUrl;
    private String coverUrl;
    private String downloadUrl;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoBean videoBean = (VideoBean) o;

        return id.equals(videoBean.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
