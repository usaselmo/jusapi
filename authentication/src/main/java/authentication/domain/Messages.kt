package authentication.domain

object Messages {
    const val USUARIO_NAO_TEM_CREDITOS: String = "Usuário não tem créditos"
    const val USUARIO_DELETADO: String = "Usuário deletado"
    const val USUARIO_CONTA_DELETADA: String = "Conta de Usuário deletada"
    const val USUARIO_CONTA_BLOQUEADA: String = "Conta de Usuário bloqueada"

    const val ERROR_AO_REGISTRAR_NOVO_USUARIO = "Erro ao registrar novo usuário"
    const val ERROR_AO_AUMENTAR_CREDITO_DE_USUARIO = "Erro ao aumentar crédito de usuário"
    const val ERROR_AO_DELETAR_USUARIO = "Erro ao deletar usuário"
    const val ERROR_AO_BLOQUEAR_USUARIO = "Erro ao bloquear usuário"
    const val ERROR_USER_NOT_FOUND = "Usuário não encontrado"
}