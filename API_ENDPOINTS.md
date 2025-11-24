# Guía de Endpoints y JSONs de la API GlamSoftt (Actualizada)

Esta guía detalla los endpoints disponibles en la API, su propósito, y los JSON necesarios para las peticiones. Se ha implementado un nuevo flujo de aprobación de citas y formularios dinámicos.

---

### **Módulo de Citas (`CitaRouter`)**

Este es el módulo más complejo, con endpoints para la creación, consulta y gestión del ciclo de vida de una cita.

#### 1. Creación de Cita (Flujo Nuevo)

*   **Endpoint:** `POST /api/citas`
*   **Propósito:** Un cliente solicita una nueva cita. Se crea con estado `PENDIENTE` y se generan notificaciones.
*   **Body (JSON Requerido):**
    ```json
    {
      "fecha": "2024-08-15",
      "hora": "11:30:00",
      "notas": "Por favor, usar productos sin sulfatos.",
      "idCliente": 5,
      "idEstilista": 1,
      "servicios": [4, 10],
      "respuestasFormulario": [
        {
          "idPregunta": 1,
          "pregunta": "¿Ha teñido su cabello en los últimos 6 meses?",
          "respuesta": "Sí"
        },
        {
          "idPregunta": 2,
          "pregunta": "¿Tiene alergias conocidas?",
          "respuesta": "A la lavanda"
        }
      ]
    }
    ```

#### 2. Consulta de Citas

*   **`GET /api/citas`**: Obtiene una lista de todas las citas.
    *   **Filtros (Query Params):** `?estado=PENDIENTE`, `?fecha=2024-08-15`
*   **`GET /api/citas/{id}`**: Obtiene los detalles de una única cita.
*   **`GET /api/citas/pendientes`**: **(Admin/Estilista)** Obtiene solo las citas en estado `PENDIENTE`.
*   **`GET /api/citas/cliente/{id}`**: Obtiene el historial de citas de un cliente.
*   **`GET /api/citas/estilista/{id}`**: Obtiene todas las citas asignadas a un estilista.
*   **`GET /api/citas/semana/{semana}/{year}`**: Obtiene las citas de una semana específica.
*   **`GET /api/citas/mes/{mes}/{year}`**: Obtiene las citas de un mes específico.
*   **`GET /api/citas/year/{year}`**: Obtiene todas las citas de un año.

#### 3. Actualización de Detalles de la Cita

*   **Endpoint:** `PUT /api/citas/{id}`
*   **Propósito:** Modificar los detalles de una cita (fecha, hora, estilista, notas). No cambia el estado.
*   **Body (JSON Requerido):** Puedes enviar solo los campos que cambian.
    ```json
    {
      "fecha": "2024-08-16",
      "hora": "12:00:00",
      "idEstilista": 2,
      "notas": "Cita reagendada."
    }
    ```

#### 4. Gestión del Estado de la Cita (Admin/Estilista)

*   **`PUT /api/citas/{id}/aprobar`**: Aprueba una cita `PENDIENTE`.
    *   **Body:** No requiere.
*   **`PUT /api/citas/{id}/rechazar`**: Rechaza una cita `PENDIENTE`.
    *   **Body:** `{"razonRechazo": "El estilista no está disponible."}`
*   **`PUT /api/citas/{id}/completar`**: Marca una cita `APROBADA` como `COMPLETADA`.
    *   **Body:** No requiere.
*   **`PUT /api/citas/{id}/cancelar`**: Cancela una cita `APROBADA`.
    *   **Body:** `{"razonRechazo": "El cliente llamó para cancelar."}`

---

### **Módulo de Formularios Dinámicos (`PreguntaRouter`)**

Este es un CRUD completo para que los administradores gestionen las preguntas de cada servicio.

*   **`GET /api/servicios/{idServicio}/preguntas`**: **(Cliente/Frontend)** Obtiene la lista de preguntas para construir el formulario de un servicio específico.
*   **`POST /api/preguntas`**: **(Admin)** Crea una nueva pregunta y la asigna a un servicio.
    *   **Body (JSON Requerido):**
        ```json
        {
          "idServicio": 4,
          "pregunta": "¿Qué tan seguido usa herramientas de calor?",
          "tipoRespuesta": "opcion_multiple",
          "opciones": ["Diariamente", "Semanalmente", "Ocasionalmente", "Nunca"],
          "obligatoria": true,
          "orden": 1,
          "activo": true
        }
        ```
*   **`PUT /api/preguntas/{id}`**: **(Admin)** Edita una pregunta existente.
*   **`DELETE /api/preguntas/{id}`**: **(Admin)** Desactiva una pregunta.

---

### **Módulo de Notificaciones (`NotificacionRouter`)**

Endpoints para que todos los usuarios gestionen sus notificaciones.

*   **`GET /api/usuarios/{idUsuario}/notificaciones`**: Obtiene la lista de todas las notificaciones de un usuario.
*   **`GET /api/usuarios/{idUsuario}/notificaciones/no-leidas`**: Obtiene el **número** de notificaciones no leídas (para el ícono de la campana).
    *   **Respuesta:** `{"status":"success", "data":{"noLeidas": 3}}`
*   **`PUT /api/notificaciones/{id}/marcar-leida`**: Marca una notificación como leída.
    *   **Body:** No requiere.

---

### **Módulo de Empleados (`EmpleadoRouter`)**

*   **`GET /api/empleados`**: Obtiene todos los empleados.
*   **`GET /api/empleados/{id}`**: Obtiene un empleado por su ID.
*   **`GET /api/empleados/rol/{idRol}`**: **(Nuevo)** Obtiene todos los empleados que pertenecen a un rol específico.
    *   **Ejemplo:** `/api/empleados/rol/2` para obtener todos los estilistas.
*   **`POST /api/empleados`**: Crea un perfil de empleado (ver documentación anterior).
*   **`PUT /api/empleados/{id}`**: Actualiza un empleado.
*   **`DELETE /api/empleados/{id}`**: Elimina un empleado.

---

### **Otros Módulos (Sin Cambios Recientes)**

Los módulos de **Servicios, Categorías, Usuarios (Register/Login), Roles, etc.**, siguen funcionando como se documentó anteriormente. Los JSON para sus operaciones `POST` y `PUT` no han cambiado.
