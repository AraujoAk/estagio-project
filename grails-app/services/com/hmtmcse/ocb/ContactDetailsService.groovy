package com.hmtmcse.ocb

class ContactDetailsService {

    private def getContactDetailsParamsParse(Contact contact, def params, Integer id = null) {
        def map = [
                id     : id,
                mobile : params.mobile,
                phone  : params.phone,
                email  : params.email,
                website: params.website,
                address: params.address,
                type   : params.type,
                contact: contact,
        ]
        return map
    }

    private def saveOrUpdate(def map) {
        ContactDetails contactDetails
        if (map && map.id) {
            contactDetails = getById(map.id) ?: new ContactDetails()
            contactDetails.properties = map
        } else {
            contactDetails = new ContactDetails(map)
        }
        contactDetails.save(flush: true)
    }

    def createOrUpdateDetails(Contact contact, def params) {
        if (params.type instanceof String) {
            saveOrUpdate(getContactDetailsParamsParse(contact, params))
        } else if (params.type && params.type.getClass().isArray()) {
            Integer index = 0
            params.type.each {
                saveOrUpdate(getContactDetailsParamsParse(contact, params, index))
                index++
            }
        }
    }

    def getById(Serializable id) {
        return ContactDetails.get(id)
    }

    def deleteContactDetails(Serializable id) {
        ContactDetails contactDetails = getById(id)
        if (contactDetails) {
            contactDetails.delete(flush: true)
            return AppUtil.infoMessage("Deleted")
        }
        return AppUtil.infoMessage("Unable to Delete", false)
    }

    def getContactDetailsListByContact(Contact contact) {
        if (contact) {
            return ContactDetails.createCriteria().list {
                eq("contact", contact)
            }
        }
        return []
    }
}
