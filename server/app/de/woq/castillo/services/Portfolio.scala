package de.woq.castillo.services

import de.woq.castillo.model.{SeminarDetails, Seminar}

/**
 * The Portfolio is the service that allows to manage the available seminars.  
 */
trait Portfolio {

  /**
   * List all available Seminars  
   */
  def list() : Seq[Seminar]

  /**
   * Create a Seminar with given details and a new <code>id</code>. The new seminar definition 
   * will be added to the list of available seminars. 
   *  
   * @param details: The details of the new Seminar to be created. 
   * @return <code>Some(s)</code> where s is the new seminar if s has been created successfully, 
   *         <code>None</code> otherwise.
   */
  def create(details : SeminarDetails) : Option[Seminar]

  /**
   * Update the a given seminar with new details. The seminar to be updated will be found 
   * using the id element of the <code>seminar</code> parameter.
   *  
   * @param seminar 
   * @return <code>Some(s)</code> if s could be updated, <code>None</code> if the seminar 
   *         couldn't be updated (i.e. it hasn't been found).
   */
  def update(seminar: Seminar) : Option[Seminar]

  /**
   * Delete a seminar identified by the parameter <code>id</code>. 
   * @param id 
   * @return <code>Some(s)</code> if s has been found and successfully deleted, <code>None</code>
   *         if the seminar couldn't found or couldn't be deleted.          
   */
  def delete(id: Long) : Option[Seminar]

  /**
   * Retrieve a seminar using the parameter <code>id</code> as a key.
   * @param id
   * @return <code>Some(s)</code> if s has been found, <code>None</code> otherwise.
   */
  def get(id: Long) : Option[Seminar]
}
