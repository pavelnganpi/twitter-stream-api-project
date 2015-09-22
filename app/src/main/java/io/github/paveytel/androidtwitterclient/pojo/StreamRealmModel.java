package io.github.paveytel.androidtwitterclient.pojo;

import io.realm.RealmObject;

/**
 * Created by z001hm0 on 9/2/15.
 */

//this is duplicate from the TwitterUserModel class, this is bad practice. But I am running out of time
//to refactor
public class StreamRealmModel extends RealmObject{

    private String name;
    private String screenName;
    private String profileImageUrl;
    private String text;
    private long createdAt;

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
