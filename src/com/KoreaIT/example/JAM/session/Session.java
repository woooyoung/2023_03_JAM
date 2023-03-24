package com.KoreaIT.example.JAM.session;

import com.KoreaIT.example.JAM.dto.Member;

public class Session {

	public Member loginedMember;
	public int loginedMemberId;

	public Session() {
		loginedMemberId = -1;
	}

}
