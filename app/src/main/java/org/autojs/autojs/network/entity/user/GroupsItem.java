package org.autojs.autojs.network.entity.user;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class GroupsItem {

    @SerializedName("createtimeISO")
    private String createtimeISO;

    @SerializedName("createtime")
    private String createtime;

    @SerializedName("private")
    private boolean jsonMemberPrivate;

    @SerializedName("hidden")
    private boolean hidden;

    @SerializedName("userTitleEnabled")
    private boolean userTitleEnabled;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("memberCount")
    private String memberCount;

    @SerializedName("icon")
    private String icon;

    @SerializedName("description")
    private String description;

    @SerializedName("labelColor")
    private String labelColor;

    @SerializedName("userTitle")
    private String userTitle;

    @SerializedName("deleted")
    private String deleted;

    @SerializedName("system")
    private boolean system;

    @SerializedName("cover:position")
    private String coverPosition;

    @SerializedName("name")
    private String name;

    @SerializedName("cover:url")
    private String coverUrl;

    @SerializedName("cover:thumb:url")
    private String coverThumbUrl;

    @SerializedName("disableJoinRequests")
    private boolean disableJoinRequests;

    @SerializedName("slug")
    private String slug;

    @SerializedName("nameEncoded")
    private String nameEncoded;

    public String getCreatetimeISO() {
        return createtimeISO;
    }

    public void setCreatetimeISO(String createtimeISO) {
        this.createtimeISO = createtimeISO;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public boolean isJsonMemberPrivate() {
        return jsonMemberPrivate;
    }

    public void setJsonMemberPrivate(boolean jsonMemberPrivate) {
        this.jsonMemberPrivate = jsonMemberPrivate;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isUserTitleEnabled() {
        return userTitleEnabled;
    }

    public void setUserTitleEnabled(boolean userTitleEnabled) {
        this.userTitleEnabled = userTitleEnabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public String getCoverPosition() {
        return coverPosition;
    }

    public void setCoverPosition(String coverPosition) {
        this.coverPosition = coverPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCoverThumbUrl() {
        return coverThumbUrl;
    }

    public void setCoverThumbUrl(String coverThumbUrl) {
        this.coverThumbUrl = coverThumbUrl;
    }

    public boolean isDisableJoinRequests() {
        return disableJoinRequests;
    }

    public void setDisableJoinRequests(boolean disableJoinRequests) {
        this.disableJoinRequests = disableJoinRequests;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNameEncoded() {
        return nameEncoded;
    }

    public void setNameEncoded(String nameEncoded) {
        this.nameEncoded = nameEncoded;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "GroupsItem{" +
                        "createtimeISO = '" + createtimeISO + '\'' +
                        ",createtime = '" + createtime + '\'' +
                        ",private = '" + jsonMemberPrivate + '\'' +
                        ",hidden = '" + hidden + '\'' +
                        ",userTitleEnabled = '" + userTitleEnabled + '\'' +
                        ",displayName = '" + displayName + '\'' +
                        ",memberCount = '" + memberCount + '\'' +
                        ",icon = '" + icon + '\'' +
                        ",description = '" + description + '\'' +
                        ",labelColor = '" + labelColor + '\'' +
                        ",userTitle = '" + userTitle + '\'' +
                        ",deleted = '" + deleted + '\'' +
                        ",system = '" + system + '\'' +
                        ",cover:position = '" + coverPosition + '\'' +
                        ",name = '" + name + '\'' +
                        ",cover:url = '" + coverUrl + '\'' +
                        ",cover:thumb:url = '" + coverThumbUrl + '\'' +
                        ",disableJoinRequests = '" + disableJoinRequests + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",nameEncoded = '" + nameEncoded + '\'' +
                        "}";
    }
}