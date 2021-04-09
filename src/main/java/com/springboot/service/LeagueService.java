package com.springboot.service;

import java.util.List;
import java.util.Optional;

import com.springboot.entity.Group;
import com.springboot.entity.Match;
import com.springboot.entity.MatchResult;
import com.springboot.entity.Participant;

public interface LeagueService {

	List<Participant> getAllParticipants();

	Optional<Participant> getParticipant(long participantId);

	Participant saveParticipant(Participant participant);

	long getParticipantCount();

	List<Group> createGroups(int numberOfParticipantsInSingleGroup);

	List<Match> getAllFirstRoundMatches();

	boolean isGroupsFormed();

	List<Group> getGroups();

	boolean isValidMatchId(String matchId);

	MatchResult getMatchResult(String matchId);

	List<MatchResult> getAllMatchResult();

	boolean isFirstRoundCompleted();

	boolean isMatchPlayed(String matchId);

	Participant getWinner();

}
