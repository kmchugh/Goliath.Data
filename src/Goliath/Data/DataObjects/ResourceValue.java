/* =========================================================
 * ResourceValue.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 5:32:19 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

/**
 * A Resource value is representative of the actual data being stored
 * as a resource.  The data is stored as a byte array.  The type of the content
 * being stored is retrieved or set by using the get and set ContentType methods.
 * The Content Type is the type of the data that is displaye to the user.  The
 * Reference type is the type of the data stored in the byte field.  If both
 * the reference type and the content type are the same, then the data stored in
 * the byte field is the final content.  If the two fields are different, then
 * the reference type is the type of the data stored in the byte array, which
 * tells the application how to refer to the final data.  For example, if the 
 * data is an image file stored on the web, the reference type could be URL, and the
 * content type could be png
 * @author kenmchugh
 */
public class ResourceValue extends UndoableDataObject<ResourceValue>
{
    private Long m_nReferenceTypeID;
    private long m_nContentTypeID;
    private byte[] m_aResourceContent;
    private boolean m_lCompressed;
    private boolean m_lEncrypted;
    private String m_cContentHash;

    private MimeType m_oContentType;
    private MimeType m_oReferenceType;

    /**
     * Gets the reference type ID for this 
     * @return the reference type for this resource value
     */
    public Long getReferenceTypeID()
    {
        canReadProperty();
        if (m_oReferenceType != null)
        {
            return m_oReferenceType.getID();
        }
        return m_nReferenceTypeID;
    }

    /**
     * Sets the reference type for this resource value
     * @param tnValue the reference type id
     */
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.MimeType.class, fieldName="ID")
    public void setReferenceTypeID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nReferenceTypeID, tnValue))
        {
            m_nReferenceTypeID = tnValue;
            if (m_oReferenceType != null && m_oReferenceType.getID() != tnValue)
            {
                m_oReferenceType = null;
            }
            propertyHasChanged();
        }
    }


    /**
     * Gets the reference type for this resource value
     * @return the reference type
     */
    @Goliath.Annotations.NotProperty
    public MimeType getReferenceType()
    {
        if (m_oReferenceType == null)
        {
            m_oReferenceType = lazyLoad(MimeType.class, m_nReferenceTypeID);
        }
        return m_oReferenceType;
    }

    /**
     * Sets the reference type for this resource value
     * @param toType the reference type
     */
    @Goliath.Annotations.NotProperty
    public void setReferenceType(MimeType toType)
    {
        if (isDifferent(m_oReferenceType, toType))
        {
            m_oReferenceType = toType;
            setReferenceTypeID(m_oReferenceType.getID());
        }
    }

    /**
     * Gets the content type for the resource value
     * @return the content type id
     */
    public long getContentTypeID()
    {
        canReadProperty();
        if (m_oContentType != null)
        {
            return m_oContentType.getID();
        }
        return m_nContentTypeID;
    }

    /**
     * Gets the content type ID for this resource value
     * @param tnValue the content type id
     */
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.MimeType.class, fieldName="ID")
    @Goliath.Annotations.NoNulls
    public void setContentTypeID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nContentTypeID, tnValue))
        {
            m_nContentTypeID = tnValue;
            if (m_oContentType != null && m_oContentType.getID() != tnValue)
            {
                m_oContentType = null;
            }
            propertyHasChanged();
        }
    }


    /**
     * Gets the content type for this resource value
     * @return the content type for this resource value
     */
    @Goliath.Annotations.NotProperty
    public MimeType getContentType()
    {
        if (m_oContentType == null)
        {
            m_oContentType = lazyLoad(MimeType.class, m_nContentTypeID);
        }
        return m_oContentType;
    }

    /**
     * Sets the content type for this resource value
     * @param toType the new content type
     */
    @Goliath.Annotations.NotProperty
    public void setContentType(MimeType toType)
    {
        if (isDifferent(m_oContentType, toType))
        {
            m_oContentType = toType;
            setContentTypeID(m_oContentType.getID());
        }
    }

    /**
     * Sets the content for this value
     * @param taContent the byte array of the content
     */
    public void setResourceContent(byte[] taContent)
    {
        canWriteProperty();
        // TODO: Implement a comparison to see if content is the same before changing
        m_aResourceContent = taContent;
        propertyHasChanged();
        
        // TODO: This needs to be implemented correctly
        m_cContentHash = Goliath.Utilities.encryptMD5(new String(taContent));
    }

    /**
     * Gets the content for this resource value
     * @return the content as a byte array
     */
    public byte[] getResourceContent()
    {
        canReadProperty();
        // TODO: Implement a comparison to see if content is the same before changing
        return m_aResourceContent;
    }
    
    // TODO: implement a setResourceContent that allows streaming of the value
    
    // TODO: Implement a getResourceContent that allows streaming of the value

    /**
     * Checks if the content that is stored in the byte array has been compressed
     * @return true if the content in the byte array is compressed
     */
    public boolean getCompressed()
    {
        canReadProperty();
        return m_lCompressed;
    }

    /**
     * Sets if the content in the byte array is compressed
     * @param tlCompressed true to ensure this content will be compressed when
     * it is stored to the data store
     */
    public void setCompressed(boolean tlCompressed)
    {
        canWriteProperty();
        if (isDifferent(m_lCompressed, tlCompressed))
        {
            m_lCompressed = tlCompressed;
            propertyHasChanged();
        }
    }
    
    /**
     * Checks if the content that is stored in the byte array has been encrypted
     * @return true if the content in the byte array is encrypted
     */
    public boolean getEncrypted()
    {
        canReadProperty();
        return m_lEncrypted;
    }

    /**
     * Sets if the content in the byte array is encrypted
     * @param tlCompressed true to ensure this content will be encrypted when
     * it is stored to the data store
     */
    public void setEncrypted(boolean tlEncrypted)
    {
        canWriteProperty();
        if (isDifferent(m_lEncrypted, tlEncrypted))
        {
            m_lEncrypted = tlEncrypted;
            propertyHasChanged();
        }
    }
    
    /**
     * The content hash is the message digest of the contents of this resource.
     * This can be used for quick comparison of resources
     * @return the content hash
     */
    public String getContentHash()
    {
        canReadProperty();
        return m_cContentHash;
    }
    
    /**
     * The content hash is a hash of the bytes of the content to create a
     * unique message digest that can quickly be compared for equality between
     * resources.
     * @param tcHash the resource hash
     */
    @Goliath.Annotations.MaximumLength(length=40)
    @Goliath.Annotations.UniqueIndex
    public void setContentHash(String tcHash)
    {
        // TODO: Implement the creation of the content hash when the content is either set or streamed.  
        // For the moment the content hash can use MD5 to create the message digest
        canWriteProperty();
        if (isDifferent(m_cContentHash, tcHash))
        {
            m_cContentHash = tcHash;
            propertyHasChanged();
        }
    }
    
    
    



}
