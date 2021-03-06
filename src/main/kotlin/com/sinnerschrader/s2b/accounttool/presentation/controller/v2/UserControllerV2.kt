package com.sinnerschrader.s2b.accounttool.presentation.controller.v2

import com.sinnerschrader.s2b.accounttool.logic.component.ldap.v2.LdapServiceV2
import com.sinnerschrader.s2b.accounttool.logic.component.ldap.v2.LdapServiceV2.UserAttributes.INFO
import com.sinnerschrader.s2b.accounttool.logic.entity.User.State
import com.sinnerschrader.s2b.accounttool.logic.entity.UserInfo
import com.sinnerschrader.s2b.accounttool.presentation.controller.v2.DynamicAllowedValues.Companion.COMPANIES
import com.sinnerschrader.s2b.accounttool.presentation.controller.v2.DynamicAllowedValues.Companion.USERTYPE
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@Api(tags = ["User"], description = "Provides access to users")
@RequestMapping("/v2")
class UserControllerV2 {
    @Autowired
    lateinit var ldapServiceV2: LdapServiceV2

    @ApiOperation("Retrieve users")
    @GetMapping("/user")
    fun getUser(@ApiParam("account state")
                @RequestParam(required = false) state: State?,
                @ApiParam("search multiple fields for a specific keyword")
                @RequestParam(required = false) searchTerm: String?,
                @ApiParam("earliest entry date")
                @RequestParam(required = false) @DateTimeFormat(iso = DATE) entryDateStart: LocalDate?,
                @ApiParam("latest entry date")
                @RequestParam(required = false) @DateTimeFormat(iso = DATE) entryDateEnd: LocalDate?,
                @ApiParam("earliest exit date")
                @RequestParam(required = false) @DateTimeFormat(iso = DATE) exitDateStart: LocalDate?,
                @ApiParam("latest exit date")
                @RequestParam(required = false) @DateTimeFormat(iso = DATE) exitDateEnd: LocalDate?,
                @ApiParam(allowableValues = COMPANIES) company: String?,
                @ApiParam(allowableValues = USERTYPE) type: String?) =
            ldapServiceV2.getUser(state = state,
                    company = company,
                    type = type,
                    searchTerm = searchTerm,
                    entryDateRange = LdapServiceV2.DateRange.of(entryDateStart, entryDateEnd),
                    exitDateRange = LdapServiceV2.DateRange.of(exitDateStart, exitDateEnd),
                    attributes = INFO)

    @ApiOperation("Retrieve user by uid")
    @GetMapping("/user/{uid}")
    fun getUser(@PathVariable uid: String) = ldapServiceV2.getUser(uid = uid, attributes = INFO).single()
}
