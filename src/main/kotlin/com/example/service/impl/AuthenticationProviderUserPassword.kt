package com.example.service.impl

import com.example.entity.User
import com.example.repository.UserRepository
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import org.reactivestreams.Publisher
import java.nio.file.attribute.UserPrincipalNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword : AuthenticationProvider {

    @Inject
    lateinit var userRepository: UserRepository

    /*@Inject
    lateinit var passwordEncoder: PasswordEncoder*/


    /*override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        var user: User? = null
        if (authenticationRequest != null) {
            user = userRepository.findByUsername(authenticationRequest?.identity.toString())
        }
        if (user == null) {
            throw UserPrincipalNotFoundException("user not found")
        }
        return Flowable.create({ emitter: FlowableEmitter<AuthenticationResponse> ->
            if (authenticationRequest != null) {
                if (authenticationRequest.identity.toString().equals("username") && authenticationRequest.secret.toString().equals("password")) {
                    emitter.onNext(UserDetails(authenticationRequest.identity as String, arrayListOf())) *//*arrayListOf(user.role)*//*
                    emitter.onComplete()
                } else {
                    emitter.onError(AuthenticationException(AuthenticationFailed()))
                }
            }
        }, BackpressureStrategy.ERROR)
    }*/

    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>?
    ): Publisher<AuthenticationResponse> {
        var user: User? = null
        if (authenticationRequest != null) {
            user = userRepository.findByUsername(authenticationRequest.identity.toString())
        }
        if (user == null) {
            throw UserPrincipalNotFoundException("user not found")
        }
        return Flowable.create({ emitter: FlowableEmitter<AuthenticationResponse> ->
            if (authenticationRequest != null) {
                if (authenticationRequest.identity.toString().equals(user.username) && authenticationRequest.secret.toString().equals(user.password)) {
                    emitter.onNext(UserDetails(authenticationRequest.identity as String, arrayListOf(user.role))) /*arrayListOf(user.role)*/
                    emitter.onComplete()
                } else {
                    emitter.onError(AuthenticationException(AuthenticationFailed()))
                }
            }
        }, BackpressureStrategy.ERROR)
    }
}
