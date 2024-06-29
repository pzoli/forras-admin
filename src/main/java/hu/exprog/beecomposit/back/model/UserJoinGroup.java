/**
 * 
 */
package hu.exprog.beecomposit.back.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="user_join_group")
public class UserJoinGroup implements Serializable {

    private static final long serialVersionUID = -448692172698838315L;

    @EmbeddedId
    private UserJoinGroupId userJoinGroupId;
	@Version
	private Long version;

    public UserJoinGroupId getUserJoinGroupId() {
        return userJoinGroupId;
    }

    public void setUserJoinGroupId(UserJoinGroupId userJoinGroup) {
        this.userJoinGroupId = userJoinGroup;
    }

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
