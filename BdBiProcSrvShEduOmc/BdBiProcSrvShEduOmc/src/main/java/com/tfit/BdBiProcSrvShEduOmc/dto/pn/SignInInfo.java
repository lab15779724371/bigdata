package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

import java.util.List;

public class SignInInfo {
	int totalPartPn;
	int actPn;
	int noPartPn;
    List<SignInConfirms> signInConfirms;
    
	public int getTotalPartPn() {
		return totalPartPn;
	}
	public void setTotalPartPn(int totalPartPn) {
		this.totalPartPn = totalPartPn;
	}
	public int getActPn() {
		return actPn;
	}
	public void setActPn(int actPn) {
		this.actPn = actPn;
	}
	public int getNoPartPn() {
		return noPartPn;
	}
	public void setNoPartPn(int noPartPn) {
		this.noPartPn = noPartPn;
	}
	public List<SignInConfirms> getSignInConfirms() {
		return signInConfirms;
	}
	public void setSignInConfirms(List<SignInConfirms> signInConfirms) {
		this.signInConfirms = signInConfirms;
	}
}
