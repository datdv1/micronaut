package com.example.service.impl

import com.example.entity.Role
import com.example.entity.User
import com.example.repository.UserRepository
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.micronaut.security.authentication.providers.PasswordEncoder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import org.reactivestreams.Publisher
import java.nio.file.attribute.UserPrincipalNotFoundException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.streams.toList

@Singleton
class AuthenticationProviderUserPassword : AuthenticationProvider {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var passwordEncoder: PasswordEncoder

    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        var user: User? = null
        if (authenticationRequest != null) {
            user = userRepository.findByUsername(authenticationRequest?.identity.toString())
        }
        if (user == null) {
            throw UserPrincipalNotFoundException("user not found")
        }
        return Flowable.create({ emitter: FlowableEmitter<AuthenticationResponse> ->
            if (authenticationRequest != null) {
                if (authenticationRequest.identity == user.username && passwordEncoder.matches(authenticationRequest.secret.toString() , user.password)) {
                    emitter.onNext(UserDetails(authenticationRequest.identity as String, user.role.map { it.name }))
                    emitter.onComplete()
                } else {
                    emitter.onError(AuthenticationException(AuthenticationFailed()))
                }
            }
        }, BackpressureStrategy.ERROR)
    }
}