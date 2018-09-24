package net.bmac.hitcounter

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(RestController::class)
class Configuration