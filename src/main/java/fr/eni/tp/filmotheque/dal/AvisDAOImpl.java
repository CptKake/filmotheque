package fr.eni.tp.filmotheque.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Membre;

@Repository
public class AvisDAOImpl implements AvisDAO {
	
	private final String FIND_BY_FILM = "SELECT ID, NOTE, COMMENTAIRE, id_membre FROM AVIS WHERE id_film = :idFilm";
	private final String INSERT = "INSERT INTO AVIS(note,commentaire,id_membre,id_film) VALUES " 
		+ " (:note, :commentaire, :idMembre, :idFilm)";

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void create(Avis avis, long idFilm) {
		// Il n’est pas nécessaire de remonter immédiatement l’identifiant auto-généré. Car les avis sont chargés par rapport à un film
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("note", avis.getNote());
		mapSqlParameterSource.addValue("commentaire", avis.getCommentaire());
		mapSqlParameterSource.addValue("idMembre", avis.getMembre().getId());
		mapSqlParameterSource.addValue("idFilm", idFilm);
		
		jdbcTemplate.update(INSERT, mapSqlParameterSource);
		
	}

	@Override
	public List<Avis> findByFilm(long idFilm) {

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("idFilm", idFilm);
		
		return jdbcTemplate.query(FIND_BY_FILM, mapSqlParameterSource, new AvisRowMapper());
	}
	
	/**
	* Classe de mapping pour gérer l'association vers Membre
	*/
	class AvisRowMapper implements RowMapper<Avis> {
		@Override
		public Avis mapRow(ResultSet rs, int rowNum) throws SQLException {
			Avis a = new Avis();
			a.setId(rs.getLong("ID"));
			a.setNote(rs.getInt("NOTE"));
			a.setCommentaire(rs.getString("COMMENTAIRE"));

			//Association vers le membre
			Membre membre = new Membre();
			membre.setId(rs.getInt("id_membre"));
			a.setMembre(membre);
			return a;
		}
	}
	

}
