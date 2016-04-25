package com.rayeye.dbutils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;


public class EntityUtils {

    // static methods only
    private EntityUtils() {
    }

    /**
     * Given an entity, returns the table name for the entity.
     * @param entity the entity to lookup.
     * @return the name of the table for the entity.
     */
    public static String getTableName(final Class<?> entity) {
        final Entity annotation = entity.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entity.getName() + " does not have the Entity annotation");
        }

        final Table table = entity.getAnnotation(Table.class);

        // get the table's name from the annotation
        if(table != null && !table.name().isEmpty()) {
            return table.name();
        } else {
            return entity.getSimpleName();
        }
    }

    
    public static String getIdColumnName(Class<?> entityClass){
         if(entityClass.getAnnotation(Entity.class) == null) {
             throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
         }

         // we need to walk up the inheritance chain
         while(entityClass != null) {
             for(Field field : entityClass.getDeclaredFields()) {
            	 Id id = field.getAnnotation(Id.class);
//                 final GeneratedValue gen = field.getAnnotation(GeneratedValue.class);
                 if( id != null) {
                     return field.getName();
                 }
             }

             // walk up the inheritance class
             
             entityClass = entityClass.getSuperclass(); 
         
             if(entityClass == null || !entityClass.isAnnotationPresent(MappedSuperclass.class)) {
             	entityClass = null;
             } 
         }

         return null;
    }
    
    /**
     * Gets the names of the columns for a given entity, except those marked as @GeneratedValue.
     * @param entity the entity to search.
     * @return a map which contains column name, and field name.
     */
    static Map<String, String> getColumnNames(Class<?> entityClass) {
        final Map<String, String> ret = new HashMap<String, String>();

        if(entityClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        // we need to walk up the inheritance chain
        while(entityClass != null) {
            for(Field field : entityClass.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                GeneratedValue gen = field.getAnnotation(GeneratedValue.class);
                Transient trans =  field.getAnnotation(Transient.class);

                // skip anything not annotated or annotated as generated
                if( gen != null || trans != null) {
                    continue;
                }

                String columnName;
                
                if( column != null ) {
               
                	// get the column name or field name
                    if(column.name().isEmpty()) {
                        columnName = field.getName();
                    } else {
                        columnName = column.name();
                    }
                    
                } else {
                	
                	columnName = field.getName();
                }

                if(ret.put(columnName, field.getName()) != null) {
                    throw new IllegalArgumentException("Entity contains two columns with the same name: " + columnName);
                }
            }

            // walk up the inheritance class
            entityClass = entityClass.getSuperclass(); 
            
            if(entityClass == null || !entityClass.isAnnotationPresent(MappedSuperclass.class)) {
            	entityClass = null;
            } 
        }

        if(ret.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        return ret;
    }
    
    static Map<String, String> getAllColumnNames(Class<?> entityClass) {
        final Map<String, String> ret = new HashMap<String, String>();

        if(entityClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        // we need to walk up the inheritance chain
        while(entityClass != null) {
            for(Field field : entityClass.getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
//                GeneratedValue gen = field.getAnnotation(GeneratedValue.class);
                Transient trans =  field.getAnnotation(Transient.class);

                // skip anything not annotated or annotated as generated
                if( trans != null) {
                    continue;
                }

                String columnName;
                
                if( column != null ) {
               
                	// get the column name or field name
                    if(column.name().isEmpty()) {
                        columnName = field.getName();
                    } else {
                        columnName = column.name();
                    }
                    
                } else {
                	
                	columnName = field.getName();
                }

                if(ret.put(columnName, field.getName()) != null) {
                    throw new IllegalArgumentException("Entity contains two columns with the same name: " + columnName);
                }
            }

            // walk up the inheritance class
            entityClass = entityClass.getSuperclass(); 
            
            if(entityClass == null || !entityClass.isAnnotationPresent(MappedSuperclass.class)) {
            	entityClass = null;
            } 
        }

        if(ret.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        return ret;
    }

    /**
     * Takes a set of strings (columns) and joins them with commas and a possible prefix.
     * @param columns the set of columns.
     * @param prefix a prefix.
     * @return the joined columns.
     */
    static String joinColumnsWithComma(final Set<String> columns, final String prefix) {
        final StringBuilder sb = new StringBuilder();

        if(columns.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        final Iterator<String> it = columns.iterator();

        if(prefix != null) {
            sb.append(prefix);
        }

        sb.append(it.next());

        while(it.hasNext()) {
            sb.append(",");

            if(prefix != null) {
                sb.append(prefix);
            }

            sb.append(it.next());
        }

        return sb.toString();
    }

    /**
     * Takes a set of strings (columns) and joins them with equals and a delimiter.
     * @param columns the columns to join.
     * @param delimiter the delimiter between the pairs.
     * @return the joined columns.
     */
    static String joinColumnsEquals(final Set<String> columns, final String delimiter) {
        final StringBuilder sb = new StringBuilder();

        if(columns.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        final Iterator<String> it = columns.iterator();

        String column = it.next();

        sb.append(column);
        sb.append(" = :");
        sb.append(column);

        while(it.hasNext()) {
            sb.append(delimiter);

            column = it.next();

            sb.append(column);
            sb.append(" = :");
            sb.append(column);
        }

        return sb.toString();
    }

}
