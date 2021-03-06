package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.UserAccount;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")
})

@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")
})

@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")
})

@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")
})

@NamedEntityGraph(name = "Study.withMembers", attributeNodes = {
        @NamedAttributeNode("members")
})

@NamedEntityGraph(name = "Study.withTagsAndZones", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones")
})


@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> managers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;
    private LocalDateTime closedDateTime;
    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;
    private boolean published;
    private boolean closed;
    private boolean useBanner;

    @Column(nullable = true)
    private int memberCount;

    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }

    public String getEncodedPath(){
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("???????????? ????????? ??? ?????? ???????????????. ???????????? ?????? ??????????????? ??????????????????.");
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("???????????? ????????? ??? ?????? ???????????????. ???????????? ???????????? ?????? ????????? ??????????????????.");
        }
    }

    public boolean canUpdateRecruiting() {
        return this.published &&
                (this.recruitingUpdatedDateTime == null ||
                this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(3)));
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("?????? ????????? ????????? ??? ????????????. ???????????? ??????????????? ??? ?????? ??? ?????? ???????????????.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("?????? ????????? ?????? ??? ????????????. ???????????? ??????????????? ??? ?????? ??? ?????? ???????????????.");
        }
    }

    public boolean isRemovable() {
        return !this.published; // TODO ????????? ?????? ???????????? ????????? ??? ??????.
    }

    public boolean checkMember(Account account) {
        return this.members.contains(account);
    }

    public void addMember(Account account) {
        this.members.add(account);
        this.memberCount++;
    }

    public boolean isManagedBy(Account account) {
        return this.getManagers().contains(account);
    }

    public void removeMember(Account account) {
        this.members.remove(account);
        this.memberCount--;
    }
}
