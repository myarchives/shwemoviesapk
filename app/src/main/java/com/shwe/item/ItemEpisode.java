package com.shwe.item;

public class ItemEpisode {

    private String id;
    private String episodeTitle;
    private String episodeUrl;
    private String episodePoster;
    private String episodeType;
    private boolean isPlaying = false;

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
}
