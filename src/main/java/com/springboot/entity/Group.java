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
public class Group {

	private String groupId;
	private int totalParticipantsInGroup;
	private List<Participant> participants;
	
}
