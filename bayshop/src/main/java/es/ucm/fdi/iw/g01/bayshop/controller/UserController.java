package es.ucm.fdi.iw.g01.bayshop.controller;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.g01.bayshop.LocalData;
import es.ucm.fdi.iw.g01.bayshop.model.User;

@Controller()
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LogManager.getLogger(UserController.class);

    @Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    /**
	 * Tests a raw (non-encoded) password against the stored one.
	 * @param rawPassword to test against
 	 * @param encodedPassword as stored in a user, or returned y @see{encodePassword}
	 * @return true if encoding rawPassword with correct salt (from old password)
	 * matches old password. That is, true if the password is correct  
	 */
	public boolean passwordMatches(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

    /**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 * for example, a possible encoding of "test" is 
	 * {bcrypt}$2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

    // Metodos de los <form> que son mas especificos de cada Controller
    @PostMapping("/create")
    @Transactional
    public String create(@ModelAttribute User newUser, @RequestParam(required=false) String pass2, Model model, HttpSession session){
        logger.warn("LLEGO AL CREATEEEEEEEEEEE", newUser);

		if(!newUser.getPassword().equals(pass2)){
			return "register";
		}

		newUser.setPassword(this.encodePassword(newUser.getPassword()));
		newUser.setEnabled((byte)1);
		newUser.setRoles("USER");
		newUser.setBaypoints(0);

		entityManager.persist(newUser);
		entityManager.flush();

        return "login";
    }

	@PostMapping("/userNameChange/{id}")
	public String changeUserName(@PathVariable long id, @ModelAttribute User edited){
		
		User target = entityManager.find(User.class, id);
		
		// Hacer update

		return "perfil";
	}

	@PostMapping("/passChange/{id}")
	public String changeUserPass(@PathVariable long id, @ModelAttribute User edited){
		User target = entityManager.find(User.class, id);

		// Hacer update

		return "perfil";
	}

	@PostMapping("/deleteAccount/{id}")
	public String deleteAccount(@PathVariable long id, @ModelAttribute User edited){
		User target = entityManager.find(User.class, id);

		// Hacer delete

		return "index";
	}
}
