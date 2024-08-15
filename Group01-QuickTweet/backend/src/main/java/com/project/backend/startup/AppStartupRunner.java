package com.project.backend.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.project.backend.model.AppAuthorization;
import com.project.backend.repository.AppAuthorizationRepository;

@Component
public class AppStartupRunner implements ApplicationRunner {
  private final AppAuthorizationRepository appAuthorizationRepository;

  public AppStartupRunner(AppAuthorizationRepository appAuthorizationRepository) {
    this.appAuthorizationRepository = appAuthorizationRepository;
  }

  /**
   * Creates a new App instance if it does not exist.
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (!appAuthorizationRepository.findById(1L).isPresent()) {
      AppAuthorization app = new AppAuthorization();
      app.setId(1L);
      appAuthorizationRepository.save(app);
    }
  }

}
