package com.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.entity.Group;
import com.springboot.entity.Match;
import com.springboot.entity.MatchResult;
import com.springboot.entity.Participant;
import com.springboot.service.EmailService;
import com.springboot.service.LeagueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("league/v1/participation")
@RequiredArgsConstructor
public class LeagueController {

	private final LeagueService leagueService;
	private final EmailService emailService;

	private static final long MAX_PARTCIPANTS = 12;

	// for getting all the participant of game.
	@GetMapping
	public ResponseEntity<?> getAllParticipants() {
		List<Participant> participants = leagueService.getAllParticipants();
		return participants.size() != 0 ? new ResponseEntity<List<Participant>>(participants, HttpStatus.OK)
				: new ResponseEntity<List<Participant>>(new ArrayList<>(), HttpStatus.NO_CONTENT);

	}

	// for getting a specific participant of game.
	@GetMapping("/{participantId}")
	public ResponseEntity<?> getParticipant(@PathVariable long participantId) {
		Optional<Participant> participant = leagueService.getParticipant(participantId);
		return participant.isPresent() ? new ResponseEntity<Participant>(participant.get(), HttpStatus.OK)
				: new ResponseEntity<String>("INVALID PARTICIPANT ID", HttpStatus.NOT_FOUND);

	}

	// for saving the participant of game.
	@PostMapping
	public ResponseEntity<?> saveParticipant(@RequestBody Participant participant) {

		if (leagueService.getParticipantCount() >= MAX_PARTCIPANTS)
			return new ResponseEntity<String>(
					"NOT MORE THAN " + MAX_PARTCIPANTS + " PARTICIPANTS ALLOWED IN THIS LEAUGE.", HttpStatus.OK);

		participant = leagueService.saveParticipant(participant);
		return new ResponseEntity<Participant>(participant, HttpStatus.CREATED);
	}

	// For creating groups of participant
	@GetMapping("/groups/{numberOfParticipantsInSingleGroup}")
	public ResponseEntity<?> createGroups(@PathVariable int numberOfParticipantsInSingleGroup) {

		if (leagueService.getParticipantCount() != MAX_PARTCIPANTS)
			return new ResponseEntity<String>(
					"FOR GROUPING THE PARTICIPANTS, WE NEED EXACT " + MAX_PARTCIPANTS + " PARTICIPANTS IN THE LEAUGE.",
					HttpStatus.OK);

		if (leagueService.isGroupsFormed())
			return new ResponseEntity<String>("TEAMS HAVE ALREADY BEEN DIVIDED INTO GROUPS. REGROUPING IS NOT POSSIBLE",
					HttpStatus.OK);

		List<Group> groups = leagueService.createGroups(numberOfParticipantsInSingleGroup);
		return new ResponseEntity<List<Group>>(groups, HttpStatus.OK);

	}

	// For getting created groups of participant
	@GetMapping("/groups")
	public ResponseEntity<?> getGroups() {

		if (!leagueService.isGroupsFormed())
			return new ResponseEntity<String>("TEAMS HAVE NOT BEEN DIVIDED INTO GROUPS YET.", HttpStatus.OK);

		List<Group> groups = leagueService.getGroups();
		return new ResponseEntity<List<Group>>(groups, HttpStatus.OK);

	}

	// for getting all first round matches of league.
	@GetMapping("/matches")
	public ResponseEntity<?> getAllFirstRoundMatches() {

		if (!leagueService.isGroupsFormed())
			return new ResponseEntity<String>("TEAMS HAVE NOT BEEN DIVIDED INTO GROUPS YET.", HttpStatus.OK);

		List<Match> matches = leagueService.getAllFirstRoundMatches();
		return new ResponseEntity<List<Match>>(matches, HttpStatus.OK);

	}

	// for getting match result between 2 competitors of game.
	@GetMapping("/result/{matchId}")
	public ResponseEntity<?> getMatchResult(@PathVariable String matchId) {

		if (!leagueService.isValidMatchId(matchId))
			return new ResponseEntity<String>("INVALID MATCH ID.", HttpStatus.NOT_FOUND);
		
		if (leagueService.isMatchPlayed(matchId))
			return new ResponseEntity<String>("MATCH HAS ALREADY BEEN PLAYED.", HttpStatus.NOT_FOUND);

		MatchResult matchResult = leagueService.getMatchResult(matchId);
		return new ResponseEntity<MatchResult>(matchResult, HttpStatus.OK);

	}

	// for getting all match result .
	@GetMapping("/result")
	public ResponseEntity<?> getAllMatchResult() {
		List<MatchResult> resultList = leagueService.getAllMatchResult();
		return new ResponseEntity<List<MatchResult>>(resultList, HttpStatus.OK);

	}

	// for getting winner of league
	// winner can be declared only when all matches of first round has been played.
	@GetMapping("/winner")
	public ResponseEntity<?> getWinner() {
		
		if (!leagueService.isFirstRoundCompleted())
				return new ResponseEntity<String>("WINNER CAN BE DECLARED ONLY IF ALL MATCHES OF FIRST ROUND HAS BEEN PLAYED", HttpStatus.OK);
		
		Participant winner = leagueService.getWinner();
		emailService.sendMail(winner.getEmail());
		return new ResponseEntity<Participant>(winner, HttpStatus.OK);

	}
	
}
