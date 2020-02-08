package com.shwe.item;

public class ItemMovie {
    private String id;
    private String movieTitle;
    private String movieDesc;
    private String moviePoster;
    private String movieCover;
    private String totalViews;
    private String rateAvg;
    private String languageId;
    private String languageName;
    private String languageBackground;
    private String movieUrl;
    private String movieType;
    private String movieHDLink;
    private String movieSDLink;

    public String getMovieHDLink() {
        return movieHDLink;
    }

    public void setMovieHDLink(String movieHDLink) {
        this.movieHDLink = movieHDLink;
    }

    public String getMovieSDLink() {
        return movieSDLink;
    }

    public void setMovieSDLink(String movieSDLink) {
        this.movieSDLink = movieSDLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieDesc() {
        return movieDesc;
    }

    public void setMovieDesc(String movieDesc) {
        this.movieDesc = movieDesc;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getMovieCover() {
        return movieCover;
    }

    public void setMovieCover(String movieCover) {
        this.movieCover = movieCover;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getRateAvg() {
        return rateAvg;
    }

    public void setRateAvg(String rateAvg) {
        this.rateAvg = rateAvg;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageBackground() {
        return languageBackground;
    }

    public void setLanguageBackground(String languageBackground) {
        this.languageBackground = languageBackground;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    @Override
    public String toString() {
        return "ItemMovie{" +
                "id='" + id + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", movieDesc='" + movieDesc + '\'' +
                ", moviePoster='" + moviePoster + '\'' +
                ", movieCover='" + movieCover + '\'' +
                ", totalViews='" + totalViews + '\'' +
                ", rateAvg='" + rateAvg + '\'' +
                ", languageId='" + languageId + '\'' +
                ", languageName='" + languageName + '\'' +
                ", languageBackground='" + languageBackground + '\'' +
                ", movieUrl='" + movieUrl + '\'' +
                ", movieType='" + movieType + '\'' +
                ", movieHDLink='" + movieHDLink + '\'' +
                ", movieSDLink='" + movieSDLink + '\'' +
                '}';
    }
}
