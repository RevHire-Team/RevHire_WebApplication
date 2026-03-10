async function fetchNotifications() {
    const userId = document.getElementById('userId').value;
    const container = document.getElementById('notifContainer');

    try {
        // CHANGE THIS LINE: Add /notifications before /api
        const response = await fetch(`/notifications/api/${userId}`);

        if (!response.ok) throw new Error("Server error");
        const notifications = await response.json();

        if (notifications.length === 0) {
            container.innerHTML = `<div class="text-center py-5 text-muted">No notifications yet.</div>`;
            return;
        }

        container.innerHTML = notifications.map(n => `
            <div class="notif-card ${n.isRead ? '' : 'unread'}">
                <div class="icon-box">
                    <i class="bi ${n.isRead ? 'bi-bell' : 'bi-bell-fill'}"></i>
                </div>
                <div class="flex-grow-1">
                    <p class="mb-0 fw-500">${n.message}</p>
                </div>
                <div class="text-end">
                    ${!n.isRead ? `<button class="btn btn-link btn-sm text-decoration-none" onclick="markAsRead(${n.notificationId})">Mark Read</button>` : ''}
                </div>
            </div>
        `).join('');
    } catch (error) {
        container.innerHTML = `<div class="alert alert-danger">Failed to load notifications.</div>`;
    }
}

// ALSO UPDATE markAsRead URL
async function markAsRead(id) {
    try {
        const response = await fetch(`/notifications/api/${id}/read`, { method: 'PUT' });
        if (response.ok) fetchNotifications();
    } catch (error) {
        console.error("Could not mark as read", error);
    }
}