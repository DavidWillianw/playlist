document.querySelector('.register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('name').value;
    const login = document.getElementById('email').value;
    const senha = document.getElementById('password').value;

    const usuario = {
        nome: nome,
        login: login,
        senha: senha
    };
    try {
        const response = await fetch("http://localhost:8080/auth/register", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuario)
        });
        if (response.ok) {
            alert('Registro bem-sucedido! Redirecionando para a página de login.');
            window.location.href = 'login.html';
        } else {
            const errorData = await response.json();
            alert('Erro no registro: ' + errorData.message);
        }
    } catch (error) {
        alert('Erro na conexão com o servidor: ' + error.message);
    }
});
