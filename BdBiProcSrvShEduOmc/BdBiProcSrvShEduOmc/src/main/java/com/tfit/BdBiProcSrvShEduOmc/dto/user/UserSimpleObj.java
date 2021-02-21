package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import lombok.Data;

@Data
public class UserSimpleObj {
	String userId;
	String userName;
	String fullName;
	String roleName;
	String accountType;
	String userOrg;
	String email;
	
	Integer lableId;
}
