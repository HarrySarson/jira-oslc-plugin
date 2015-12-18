package com.ericsson.jira.oslc.provider;

/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *******************************************************************************/

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.provider.json4j.AbstractOslcRdfJsonProvider;

@Provider
@Produces(OslcMediaType.APPLICATION_JSON)
@Consumes(OslcMediaType.APPLICATION_JSON)
public class OslcRdfJsonArrayProvider
       extends AbstractOslcRdfJsonProvider
       implements MessageBodyReader<Object[]>, 
                  MessageBodyWriter<ResponseArrayWrapper<?>>
{
    public OslcRdfJsonArrayProvider()
    {
        super();
    }

    @Override
    public long getSize(final ResponseArrayWrapper<?>     objects,
                        final Class<?>     type,
                        final Type         genericType,
                        final Annotation[] annotation,
                        final MediaType    mediaType)
    {
        return -1;
    }

    @Override
    public boolean isWriteable(final Class<?>     type,
                               final Type         genericType,
                               final Annotation[] annotations,
                               final MediaType    mediaType)
    {
      if(ResponseArrayWrapper.class.isAssignableFrom(type)){
      return 
          (isWriteable(
               annotations,
               OslcMediaType.APPLICATION_JSON_TYPE,
               mediaType)); 
      }
      return false;
    }
    
  protected static boolean isWriteable(final Annotation[] annotations, final MediaType requiredMediaType, final MediaType actualMediaType) {

      // When handling "recursive" writing of an OSLC Error object, we get a
      // zero-length array of annotations
      if ((annotations != null) && ((annotations.length > 0))) {
        for (final Annotation annotation : annotations) {
          if (annotation instanceof Produces) {
            final Produces producesAnnotation = (Produces) annotation;

            for (final String value : producesAnnotation.value()) {
              if (requiredMediaType.isCompatible(MediaType.valueOf(value))) {
                return true;
              }
            }
          }
        }

        return false;
      }

      // We do not have annotations when running from the non-web client.
      return requiredMediaType.isCompatible(actualMediaType);

  }

    @Override
    public void writeTo(final ResponseArrayWrapper<?>                       objects,
                        final Class<?>                       type,
                        final Type                           genericType,
                        final Annotation[]                   annotations,
                        final MediaType                      mediaType,
                        final MultivaluedMap<String, Object> map,
                        final OutputStream                   outputStream)
           throws IOException,
                  WebApplicationException
    {
      
      Object[] resources =objects.getResource();
        writeTo(true,
            resources,
                mediaType,
                map,
                outputStream);
    }
    @Override
    public boolean isReadable(final Class<?>   type,
                  final Type     genericType,
                  final Annotation[] annotations,
                  final MediaType  mediaType)
    {
      return (type.isArray()) &&
           (isReadable(type.getComponentType(),
                 OslcMediaType.APPLICATION_JSON_TYPE,
                 mediaType));
    }

    @Override
    public Object[] readFrom(final Class<Object[]>          type,
                 final Type               genericType,
                 final Annotation[]           annotations,
                 final MediaType            mediaType,
                 final MultivaluedMap<String, String> map,
                 final InputStream            inputStream)
         throws IOException,
            WebApplicationException
    {
      return readFrom(type.getComponentType(),
              mediaType,
              map,
              inputStream);
    } 
}