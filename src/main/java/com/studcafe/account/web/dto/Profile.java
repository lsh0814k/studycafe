package com.studcafe.account.web.dto;

import com.studcafe.account.domain.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
@NoArgsConstructor
public class Profile {
    @Length(max = 35)
    private String bio;
    @Length(max = 50)
    private String url;
    @Length(max = 50)
    private String occupation;
    @Length(max = 50)
    private String location;

    private String profileImage;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();
    }

    public Account createAccount() {
        return Account.builder()
                .bio(this.bio)
                .url(this.url)
                .occupation(this.occupation)
                .location(this.location)
                .profileImage(this.profileImage)
                .build();
    }
}
