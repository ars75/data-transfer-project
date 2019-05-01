package org.datatransferproject.spi.cloud.storage;

import org.datatransferproject.spi.cloud.types.JobAuthorization;
import org.datatransferproject.spi.cloud.types.PortabilityJob;
import org.datatransferproject.types.transfer.errors.ErrorDetail;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

/**
 * A store for {@link PortabilityJob}s.
 *
 * <p>This class is intended to be implemented by extensions that support storage in various
 * back-end services.
 */
public interface JobStore extends TemporaryPerJobDataStore {

  interface JobUpdateValidator {

    /**
     * Validation to do as part of an atomic update. Implementers should throw an {@code
     * IllegalStateException} if the validation fails.
     */
    void validate(PortabilityJob previous, PortabilityJob updated);
  }

  /**
   * Inserts a new {@link PortabilityJob} keyed by {@code jobId} in the store.
   *
   * <p>To update an existing {@link PortabilityJob} instead, use {@link #update}.
   *
   * @throws IOException if a job already exists for {@code job}'s ID, or if there was a different
   * problem inserting the job.
   */
  void createJob(UUID jobId, PortabilityJob job) throws IOException;

  /**
   * Verifies a {@code PortabilityJob} already exists for {@code jobId}, and updates the entry to
   * {@code job}.
   *
   * @throws IOException if a job didn't already exist for {@code jobId} or there was a problem
   * updating it
   */
  void updateJob(UUID jobId, PortabilityJob job) throws IOException;

  /**
   * Verifies a {@code PortabilityJob} already exists for {@code jobId}, and updates the entry to
   * {@code job}. If {@code validator} is non-null, validator.validate() is called first, as part of
   * the atomic update.
   *
   * @throws IOException if a job didn't already exist for {@code jobId} or there was a problem
   * updating it
   * @throws IllegalStateException if validator.validate() failed
   */
  void updateJob(UUID jobId, PortabilityJob job, JobUpdateValidator validator) throws IOException;

  /**
   * Stores errors related to a transfer job.
   *
   * @throws IOException if a job didn't already exist for {@code jobId} or there was a problem
   * updating it
   */
  void addErrorsToJob(UUID jobId, Collection<ErrorDetail> errors) throws IOException;

  /**
   * Removes the {@link PortabilityJob} in the store keyed by {@code jobId}.
   *
   * @throws IOException if the job doesn't exist, or there was a different problem deleting it.
   */
  void remove(UUID jobId) throws IOException;

  /**
   * Returns the job for the id or null if not found.
   *
   * @param jobId the job id
   */
  PortabilityJob findJob(UUID jobId);

  /**
   * Gets the ID of the first {@link PortabilityJob} in state {@code jobState} in the store, or null
   * if none found.
   */
  UUID findFirst(JobAuthorization.State jobState);
}
