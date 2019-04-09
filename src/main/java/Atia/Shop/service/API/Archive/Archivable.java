/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Archive;

import Atia.Shop.config.userRoles.UserRolesEnum;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface Archivable<Entity, ID> {
    
    boolean archive(Entity entity, String userEmail);
    
    boolean isArchivable(Entity entity);
    
    List<Entity> getAllArchivesByCreator(String creatorMail, UserRolesEnum role);
    
    List<Entity> getAllArchives(UserRolesEnum role);
    
    Entity getArchiveById(ID id, String userEmail, UserRolesEnum role);
    
    boolean deleteArchiveById(ID id,  UserRolesEnum role);
}
