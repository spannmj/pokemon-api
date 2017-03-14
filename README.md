# pokemon-api

A limited pokemon rest api built with spring boot using pokeapi.co for pokemon data

GET pokemon/{name Or id}
  Will return a json response consisiting of the id, name, stats and moves that a pokemon has.
  The results of the request are cached, so lookups of the same pokemon will not have to make
  a complete round trip to pokeapi.co, speeding up requests.
  If a name Or id doesn't exist, an error message is returned

GET attack/{name Or id}
  Will return a json reponse consisting of the id, name, power, accuracy, priority and power points (pp).
  The results of this request are also cached for faster subsequent lookups
  If a name Or Id doesn't exist, an error message is returned
  
GET battle?pokemon1={name or id}&pokemon2={name or id}
  Will return a json response consisting of the winner pokemon, loser pokemon, and the attacks
  issued during the battle in chronological order from first attack to last.
  If a name or Id of a pokemon doesn't exist, an error message is returned.




