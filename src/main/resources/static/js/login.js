document.querySelector("form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const login = document.getElementById("email").value;
    const senha = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ login, senha })
        });

        const data = await response.json();

        if (!response.ok) {
            alert(data.erro || "Erro ao fazer login");
            return;
        }

        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioNome", data.nome);
        localStorage.setItem("usuarioLogin", data.login);
        localStorage.setItem("usuarioId", data.id);
        
        window.location.href = "menu.html";

    } catch (error) {
        alert("Erro de conexão com o servidor");
    }
});
