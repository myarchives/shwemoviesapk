package com.shwe.item;

public class ItemEpisode {

    private String id;
    private String episodeTitle;
    private String episodeUrl;
    private String episodeHDLink;
    private String getEpisodeSDLink;
    private String episodePoster;
    private String episodeType;
    private boolean isPlaying = false;

    public String getEpisodeHDLink() {
        return episodeHDLink;
    }

    public void setEpisodeHDLink(String episodeHDLink) {
        this.episodeHDLink = episodeHDLink;
    }

    public String getGetEpisodeSDLink() {
        return getEpisodeSDLink;
    }

    public void setGetEpisodeSDLink(String getEpisodeSDLink) {
        this.getEpisodeSDLink = getEpisodeSDLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
    }

    public String getEpisodePoster() {
        return episodePoster;
    }

    public void setEpisodePoster(String episodePoster) {
        this.episodePoster = episodePoster;
    }

    public String getEpisodeType() {
        return episodeType;
    }

    public void setEpisodeType(String episodeType) {
        this.episodeType = episodeType;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public String toString() {
        return "ItemEpisode{" +
                "id='" + id + '\'' +
                ", episodeTitle='" + episodeTitle + '\'' +
                ", episodeUrl='" + episodeUrl + '\'' +
                ", episodeHDLink='" + episodeHDLink + '\'' +
                ", getEpisodeSDLink='" + getEpisodeSDLink + '\'' +
                ", episodePoster='" + episodePoster + '\'' +
                ", episodeType='" + episodeType + '\'' +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
