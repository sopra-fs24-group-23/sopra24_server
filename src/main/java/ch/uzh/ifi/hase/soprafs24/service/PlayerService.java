package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Player Service
 * This class is the "worker" and responsible for all
 * functionality related to players
 */
@Service
@Transactional
public class PlayerService {

  private final Logger log = LoggerFactory.getLogger(PlayerService.class);

}
