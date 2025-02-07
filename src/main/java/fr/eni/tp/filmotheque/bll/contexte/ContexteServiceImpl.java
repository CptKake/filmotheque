package fr.eni.tp.filmotheque.bll.contexte;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dal.MembreDAO;

@Service
@Primary
public class ContexteServiceImpl implements ContexteService {

	private MembreDAO membreDAO;
	
	public ContexteServiceImpl(MembreDAO membreDAO) {
		this.membreDAO = membreDAO;
	}
	
	
	@Override
	public Membre charger(String email) {
		return this.membreDAO.read(email);
	}

}
