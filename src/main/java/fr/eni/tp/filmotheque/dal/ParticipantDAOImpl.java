package fr.eni.tp.filmotheque.dal;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Participant;

@Repository
public class ParticipantDAOImpl implements ParticipantDAO {

	private final String FIND_BY_ID = "SELECT id, nom, prenom from PARTICIPANT WHERE id = :id";
	private final String FIND_ALL = "SELECT id, nom, prenom from PARTICIPANT";
	private final String INSERT = "INSERT INTO ACTEURS(id_film, id_participant) VALUES (:idFilm, :idParticipant)";
	private final String FIND_PARTICIPANTS = "SELECT id, nom, prenom FROM PARTICIPANT WHERE id IN (:listeId)";

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;


	@Override
	public Participant read(long id) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("id", id);
		return jdbcTemplate.queryForObject(FIND_BY_ID, mapSqlParameterSource, new BeanPropertyRowMapper<>(Participant.class));
	}


	@Override
	public List<Participant> findAll() {
		return jdbcTemplate.query(FIND_ALL, new BeanPropertyRowMapper<>(Participant.class));
	}


	@Override
	public List<Participant> findActeurs(long idFilm) {

		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate.getJdbcTemplate()).withProcedureName("FindActeurs").returningResultSet("acteurs", new BeanPropertyRowMapper<>(Participant.class));
		
		SqlParameterSource in = new MapSqlParameterSource().addValue("idFilm", idFilm);
		
		Map<String, Object> out = jdbcCall.execute(in);
		
		if (out.get("acteurs") != null) {
			List<Participant> acteurs = (List<Participant>) out.get("acteurs");
			return acteurs;
		}
		return null;
	}


	@Override
	public void createActeur(long idParticipant, long idFilm) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("idFilm", idFilm);
		mapSqlParameterSource.addValue("idParticipant", idParticipant);
		jdbcTemplate.update(INSERT, mapSqlParameterSource);
		
	}


	@Override
	public List<Participant> findParticipants(List<Participant> listeParticipants) {
		List<Long> listeId = listeParticipants.stream().map(Participant::getId).toList();
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("listeId", listeId);
		
		return jdbcTemplate.query(FIND_PARTICIPANTS, map, new BeanPropertyRowMapper<>(Participant.class));
	}
}
