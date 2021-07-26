package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Component;

import javax.persistence.*;
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

    public String getImage() {
        return image != null ? image : "/images/default-banner.png";
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
}
