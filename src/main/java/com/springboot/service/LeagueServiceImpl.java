package com.springboot.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.springboot.entity.Group;
import com.springboot.entity.Match;
import com.springboot.entity.MatchResult;
import com.springboot.entity.Participant;
import com.springboot.utility.Partition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeagueServiceImpl implements LeagueService {
	
	private static List<Participant> participantList = new ArrayList<>();
	private static List<Group> groupList = new ArrayList<>();
	private static List<Match> matchList = new ArrayList<>();
	private static List<MatchResult> resultList = new ArrayList<>();
	private static Participant winner = null;
	
	private static final String MATCH_NOT_PLAYED_YET = "MATCH_NOT_PLAYED_YET";
	private static final String MATCH_PLAYED = "MATCH_PLAYED";
	
	@Value("${test.email.to}")
	private String email;

	public List<Participant> getAllParticipants() {
		return participantList;
	}

	public Optional<Participant> getParticipant(long participantId) {
		return participantList.stream().filter(participant -> participant.getParticipantId()==participantId).findFirst();
	}

	@Override
	public Participant saveParticipant(Participant participant) {
		participant.setParticipantId(generateParticipantId());
		participantList.add(participant);
		return participant;
	}

	@Override
	public long getParticipantCount() {
		return participantList.size();
	}

	@Override
	public List<Group> createGroups(int numberOfParticipantsInSingleGroup) {

		Collections.shuffle(participantList);
		
		int size = participantList.size();
		Partition<Participant> s = Partition.ofSize(participantList, numberOfParticipantsInSingleGroup);

		Group group = null;
		
		for (int i = 0; i < size/numberOfParticipantsInSingleGroup; i++) {
			group = new Group("GROUP-"+(i+1),numberOfParticipantsInSingleGroup, s.get(i));
			groupList.add(group);
		}
			return groupList;
	}

	@Override
	public List<Match> getAllFirstRoundMatches() {
		
		if(!matchList.isEmpty())
			return matchList;
		
		groupList.stream().forEach(group -> {
			List<Participant> pl = group.getParticipants();
			
			// match_list that contains 2 participants, who will play against each other 
			List<Participant> match_list = null;
			for(int i=0;i<pl.size();i+=2) {
				match_list = new ArrayList<Participant>();
				match_list.add(pl.get(i));
				match_list.add(pl.get(i+1));
				matchList.add(new Match(generateMatchId(),group.getGroupId(),match_list,MATCH_NOT_PLAYED_YET));
			}
		});
		return matchList;
	}
	

	@Override
	public boolean isGroupsFormed() {
		return groupList.size()!=0;
	}
	
	@Override
	public List<Group> getGroups() {
		return groupList;
	}
	
	@Override
	public boolean isValidMatchId(String matchId) {
		return matchList.stream().filter(match -> match.getMatchId().equals(matchId)).count()>0;	
	}
	
	// loading 10 participant into DB at application load time.
	@PostConstruct
	private void loadDataIntoH2Database() {

		// for testing purpose we have given same emailId
        Stream.of(
        		new Participant("Mike","US",25,email),
        		new Participant("Jack","ENG",25,email),
        		new Participant("Ross","NZ",25,email),
        		new Participant("Ken","WI",25,email),
        		new Participant("Clark","AUS",36,email),
        		new Participant("Adam","SL",25,email),
        		new Participant("Tanaka","JP",25,email),
        		new Participant("Akram","PAK",25,email),
        		new Participant("Pankaj","IN",25,email),
        		new Participant("Kemp","SA",25,email)				
        ).forEach(participant -> {
        	participant.setParticipantId(generateParticipantId());
        	participantList.add(participant); 	
        });
    }
	
	public static long generateParticipantId() {
		return new Date().getTime();
	}
	
	public static String generateMatchId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public MatchResult getMatchResult(String matchId) {
		MatchResult matchResult = new MatchResult();
		 Match match = matchList.stream().filter(m -> m.getMatchId().equals(matchId))
				 				.peek(s -> s.setStatus(MATCH_PLAYED))
				 				.collect(Collectors.toList()).get(0);
		 
			matchResult.setMatchId(match.getMatchId());
			matchResult.setGroupId(match.getGroupId());
			
			if(Math.random()%2==0) {
			matchResult.setWinner(match.getParticipants().get(0));
			matchResult.setLoser(match.getParticipants().get(1));
			}else {
			matchResult.setWinner(match.getParticipants().get(1));
			matchResult.setLoser(match.getParticipants().get(0));	
			}

		resultList.add(matchResult);
		return matchResult;
	}

	@Override
	public List<MatchResult> getAllMatchResult() {
		return resultList;
	}

	@Override
	public boolean isFirstRoundCompleted() {
		return resultList.size()==6;
	}

	@Override
	public boolean isMatchPlayed(String matchId) {
	return	resultList.stream().anyMatch(match -> match.getMatchId().equals(matchId));
	}

	@Override
	public Participant getWinner() {
		
		if(winner!=null)
			return winner;
		
		Collections.shuffle(resultList);
		Random objGenerator = new Random();
		
		// To generate a random number from 0 to 6
		MatchResult finalMatchResult = resultList.get(objGenerator.nextInt(6));
		
		winner =  participantList.stream()
						.filter(p -> p.getParticipantId()==finalMatchResult.getWinner().getParticipantId()).findFirst().get();
		
		return winner;

	}
}
