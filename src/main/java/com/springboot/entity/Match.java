package com.springboot.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Match {

	private String matchId;
	private String groupId;
	private List<Participant> participants;
	private String status; // whether the match is played or not yet
	
}
