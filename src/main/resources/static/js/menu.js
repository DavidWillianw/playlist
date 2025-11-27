document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Você precisa fazer login primeiro!");
        window.location.href = "login.html";
        return;
    }

    const response = await fetch("http://localhost:8080/auth/validate", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    if (!response.ok) {
        alert("Sessão expirada! Faça login novamente.");
        localStorage.clear();
        window.location.href = "login.html";
        return;
    }

    async function carregarPlaylists() {
        const resp = await fetch("http://localhost:8080/playlists", {
            headers: { "Authorization": "Bearer " + token }
        });

        const lista = document.getElementById("listaPlaylists");
        lista.innerHTML = "";

        if (!resp.ok) {
            lista.innerHTML = `<p>Erro ao carregar playlists.</p>`;
            return;
        }

        const playlists = await resp.json();

        playlists.forEach(p => {
            lista.innerHTML += `
                <div class="playlist-card" onclick="abrirPlaylist(${p.id})">
                    <img src="${p.foto ?? 'images/default-cover.png'}" class="playlist-img">
                    <h3>${p.nome}</h3>
                    <p>${p.musicas.length} músicas</p>
                </div>
            `;
        });
    }

    carregarPlaylists();

    const modal = document.getElementById("modalCriar");
    const btnNova = document.getElementById("btnNovaPlaylist");

    btnNova.onclick = () => modal.classList.remove("hidden");
    modal.querySelector(".btn-close").onclick = () => modal.classList.add("hidden");

    document.getElementById("salvarPlaylist").onclick = async () => {
        const nome = document.getElementById("nomePlaylist").value;
        const desc = document.getElementById("descPlaylist").value;

        const resp = await fetch("http://localhost:8080/playlists", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nome: nome,
                foto: null,
                descricao: desc
            })
        });

        if (resp.ok) {
            modal.classList.add("hidden");
            carregarPlaylists();
        } else {
            alert("Erro ao criar playlist.");
        }
    };

    document.getElementById("logoutBtn").onclick = () => {
        localStorage.clear();
        window.location.href = "login.html";
    };

});

function abrirPlaylist(id) {
    window.location.href = `playlist.html?id=${id}`;
}
