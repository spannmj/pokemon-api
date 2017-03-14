package com.pokemon.api.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.pokemon.api.rest.domain.Attack;
import com.pokemon.api.rest.domain.BattleHistory;
import com.pokemon.api.rest.domain.Move;
import com.pokemon.api.rest.domain.MoveLookup;
import com.pokemon.api.rest.domain.NotFound;
import com.pokemon.api.rest.domain.Pokemon;
import com.pokemon.api.rest.domain.PokemonMove;
import com.pokemon.api.rest.domain.PokemonStat;

@RestController
public class PokemonController {

	Map<Integer, Pokemon> pokemonStorage = new HashMap<Integer, Pokemon>();
	Map<Integer, Move> moveStorage = new HashMap<Integer, Move>();
	
	Map<String, Integer> pokemonNameMapping = new HashMap<String, Integer>();
	Map<String, Integer> moveNameMapping = new HashMap<String, Integer>();
	
	
    @RequestMapping("/pokemon/{nameOrId}")
    public ResponseEntity<?> pokemon(@PathVariable String nameOrId) {
    	Pokemon pokemon = pokemonLookUp(nameOrId);
    	if( pokemon == null){
    		NotFound notFound = new NotFound("Not Found.");
    		return ResponseEntity.ok(notFound); 
    	};
    	
    	return ResponseEntity.ok(pokemon);
    } 
    
    
    @RequestMapping("/attack/{nameOrId}")
    public ResponseEntity<?> attack(@PathVariable String nameOrId) {
    	Move move = moveLookUp(nameOrId);
    	if( move == null){
    		NotFound notFound = new NotFound("Not Found.");
    		return ResponseEntity.ok(notFound); 
    	};
    	
    	return ResponseEntity.ok(move);
    }
    
    @RequestMapping("/battle")
    public ResponseEntity<?> attack(@RequestParam( name="pokemon1" )String poke1NameId,
    		@RequestParam( name="pokemon2" )String poke2NameId) {
    	BattleHistory battleHistory = new BattleHistory();	
    	Pokemon pokemon1 = pokemonLookUp(poke1NameId);
    	Pokemon pokemon2 = pokemonLookUp(poke2NameId);
    	
    	if(pokemon1 == null || pokemon2 == null){
    		NotFound notFound = new NotFound("One Or Both Pokemon Do Not Exist.");
    		return ResponseEntity.ok(notFound); 
    	}
    	
    	Double pokemon1hp = getHp(pokemon1).doubleValue();
    	Double pokemon2hp = getHp(pokemon2).doubleValue();
    	List<PokemonMove> pokemon1Moves = pokemon1.getMoves();
    	List<PokemonMove> pokemon2Moves = pokemon2.getMoves();
    	Move pickedMove = null;
    	Boolean isPokemonOneTurn = true;
    	List<Attack> attacks = new ArrayList<Attack>(); 
    	Attack attack = null;
    	int power = 0;
    	while(pokemon1hp >0 && pokemon2hp >0){
    		if(isPokemonOneTurn){
    			pickedMove = pickMoveRandomly(pokemon1Moves);
    			power = pickedMove.getPower() == null ? 0 : pickedMove.getPower();
    			pokemon2hp = pokemon2hp - ( power * .1);
    			attack = new Attack(pokemon1.getName(),pickedMove);
    			isPokemonOneTurn = false;
    		}
    		else{
    			pickedMove = pickMoveRandomly(pokemon2Moves);
    			power = pickedMove.getPower() == null ? 0 : pickedMove.getPower();
    			pokemon1hp = pokemon1hp - (power * .1);
    			attack = new Attack(pokemon2.getName(),pickedMove);
    			isPokemonOneTurn = true;
    		}
    		attacks.add(attack);
    	}
    	
    	battleHistory.setAttacks(attacks);
    		
    	if(pokemon1hp > 0){
    		battleHistory.setWinner(pokemon1);
    		battleHistory.setLoser(pokemon2);
    	}
    	else{
    		battleHistory.setWinner(pokemon2);
    		battleHistory.setLoser(pokemon1);
    	}
    	
    	return ResponseEntity.ok(battleHistory);
    }
    	
    
    private Pokemon pokemonLookUp(String nameOrId){
    	
    	Pokemon pokemon = null;
    	try{
	    	if(StringUtils.isNumeric(nameOrId)){
	    		pokemon = pokemonStorage.get(Integer.parseInt(nameOrId));
	    	}
	    	else{
	    		Integer id = pokemonNameMapping.get(nameOrId);
	    		pokemon = pokemonStorage.get(id);
	    	}
    	}catch(NumberFormatException e){
    		return pokemon;
    	}
 
    	if(pokemon == null){
    	
	    	RestTemplateBuilder builder = new RestTemplateBuilder();
	    	RestTemplate template = builder.build();
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("User-Agent", "poke api");
	    	HttpEntity<String> entity = new HttpEntity<String>(headers);
	    	try{
		    	ResponseEntity<Pokemon> pokemonResponse = template.exchange( "https://pokeapi.co/api/v2/pokemon/"+nameOrId
		    			,HttpMethod.GET
		    			,entity
		    			,Pokemon.class);
		    	
		    	pokemon = pokemonResponse.getBody();
		    	pokemonStorage.put(pokemon.getId(), pokemon);
		    	pokemonNameMapping.put(pokemon.getName(), pokemon.getId());
	    	}
	    	catch(Exception e){
	    		return pokemon;
	    	}
    	}
    	
        return pokemon;
    }
    
    
    private Move moveLookUp(String nameOrId){

    	Move move = null;

    	try{
	    	if(StringUtils.isNumeric(nameOrId)){
	    		move = moveStorage.get(Integer.parseInt(nameOrId));
	    	}
	    	else{
	    		Integer id = moveNameMapping.get(nameOrId);
	    		move = moveStorage.get(id);
	    	}
    	}catch(NumberFormatException e){
    		return move;
    	}
    	if(move == null){
	    	RestTemplateBuilder builder = new RestTemplateBuilder();
	    	RestTemplate template = builder.build();
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("User-Agent", "poke api");
	    	HttpEntity<String> entity = new HttpEntity<String>(headers);
	    	try{
		    	ResponseEntity<Move> moveResponse = template.exchange( "https://pokeapi.co/api/v2/move/"+nameOrId
		    			,HttpMethod.GET
		    			,entity
		    			,Move.class);
		    	move = moveResponse.getBody();
		    	moveStorage.put(move.getId(), move);
		    	moveNameMapping.put(move.getName(), move.getId());
	    	}
		    catch(Exception E){
		    	return move;
		    }
    	}
    			
        return move;
    }
    
    private Integer getHp(Pokemon pokemon){
    	
    	for(PokemonStat pokeStat : pokemon.getStats()){
    		if(pokeStat.getStat().getName().equals("hp")){
    			return pokeStat.getBaseStat();
    		}
    	}
    	
    	return 0;
    }
    
		
    private Move pickMoveRandomly(List<PokemonMove> moves){
    	
    	Random random = new Random();
    	int moveNumber = random.nextInt(moves.size());
    	PokemonMove chosenMove = moves.get(moveNumber);
    	MoveLookup  moveLookup = chosenMove.getMove();
    	return moveLookUp(moveLookup.getName());
	
    }
}
