package com.hmtmcse.ocb

import grails.web.servlet.mvc.GrailsParameterMap
import javax.servlet.http.HttpServletRequest
import com.hmtmcse.ocb.ContactDetails

class ContactService {

    AuthenticationService authenticationService
    ContactDetailsService contactDetailsService

    def save(GrailsParameterMap params, HttpServletRequest request) {
        Contact contact = new Contact(params)
        contact.member = authenticationService.getMember()
        def response = AppUtil.saveResponse(false, contact)
        if (contact.validate()) {
            contact.save(flush: true)
            if (!contact.hasErrors()) {
                response.isSuccess = true
                contactDetailsService.createOrUpdateDetails(contact, params)
                uploadImage(contact, request)
            }
        }
        return response
    }

    def update(Contact contact, params, HttpServletRequest request) {
        try {
            contact.properties = params

            if (params.details && params.details instanceof List) {
                contact.details.clear()
                params.details.each { detailParams ->
                    ContactDetails detail = new ContactDetails(detailParams)
                    contact.addToDetails(detail)
                }
            }

            if (!contact.save(flush: true)) {
                return [isSuccess: false, model: contact]
            }

            return [isSuccess: true, model: contact]
        } catch (Exception e) {
            log.error("Erro ao atualizar contato: ${e.message}")
            return [isSuccess: false, model: contact]
        }
    }

    def getStatus(Serializable id) {
        Contact contact = Contact.get(id)
        if (!contact) {
            return "Contact not found"
        }
        return contact.image ? "Contact has an image" : "Contact does not have an image"
    }

    def get(Serializable id) {
        return Contact.get(id)
    }

    def list(GrailsParameterMap params) {
        params.max = params.max ?: GlobalConfig.itemsPerPage()
        List<Contact> contactList = Contact.createCriteria().list(params) {
            if (params?.colName && params?.colValue) {
                like(params.colName, "%" + params.colValue + "%")
            }
            if (!params.sort) {
                order("id", "desc")
            }
            eq("member", authenticationService.getMember())
        }
        return [list: contactList, count: contactList.totalCount]
    }

    def delete(Contact contact) {
        try {
            contact.delete(flush: true)
        } catch (Exception e) {
            println(e.getMessage())
            return false
        }
        return true
    }

    def uploadImage(Contact contact, HttpServletRequest request) {
        if (request.getFile("contactImage") && !request.getFile("contactImage").filename.equals("")) {
            String image = FileUtil.uploadContactImage(contact.id, request.getFile("contactImage"))
            if (!image.equals("")) {
                contact.image = image
                contact.save(flush: true)
            }
        }
    }
}
