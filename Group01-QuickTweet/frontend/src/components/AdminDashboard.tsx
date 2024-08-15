import { User } from "../types";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { Button, Card, Container, Form, ListGroup, Spinner } from "react-bootstrap";
import Pagination from "react-bootstrap/Pagination";
import Footer from "./Footer";
import Nav from "./Nav";
import {
  acceptUserRequest,
  changeUserRole,
  deleteUserAccount,
  getAllUsers,
  getPendingRequests,
  rejectUserRequest,
} from "../api/userApi";
import ErrorToast from "./ErrorToast";

/**
 * AdminDashboard component serves as the main interface for administrators to manage users.
 * It provides functionalities such as viewing all users, managing pending user requests, changing user roles,
 * and deleting user accounts. The component fetches and displays user data using pagination.
 * It also includes an ErrorToast component to display error messages.
 *
 * The component uses React Bootstrap for UI components and react-bootstrap/Pagination for pagination controls.
 * State management is handled with useState for tracking users, pending requests, loading and error states.
 * useEffect is used to fetch user data on component mount or when relevant state changes.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {User} props.user - The current user object, expected to be an administrator.
 * @param {Dispatch<SetStateAction<boolean>>} props.setAuth - Function to update authentication state in the parent component.
 */
const AdminDashboard = ({
  user,
  setAuth,
}: {
  user: User;
  setAuth: Dispatch<SetStateAction<boolean>>;
}) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [users, setUsers] = useState<User[]>([]);
  const [pendingRequests, setPendingRequests] = useState<User[]>([]);
  const [pending, setPending] = useState(true);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [show, setShow] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      if (!user.username) {
        return;
      }

      setLoading(true);
      try {
        const data = await getAllUsers();
        setUsers(data.filter(u => u.username != user.username));

        const requests = await getPendingRequests(user.username);
        setPendingRequests(requests);
      } catch (err: any) {
        setError(err.request.responseText);
      }

      setLoading(false);
    };

    if (loading) {
      fetchUsers();
    }
  }, [user.username, loading]);

  const totalItems = () => {
    if (pending) {
      return Math.ceil(pendingRequests.length / 5);
    } else {
      return Math.ceil(users.length / 5);
    }
  };

  const startIndex = (currentPage - 1) * 5;
  const endIndexUsers = Math.min(startIndex + 5, users.length);
  const endIndexRequests = Math.min(startIndex + 5, pendingRequests.length);
  const currentUsers = users.slice(startIndex, endIndexUsers);
  const currentRequests = pendingRequests.slice(startIndex, endIndexRequests);
  const totalPages = totalItems();

  const paginationItems = [];
  for (let i = 1; i <= totalPages; i++) {
    paginationItems.push(
      <Pagination.Item key={i} active={i === currentPage} onClick={() => setCurrentPage(i)}>
        {i}
      </Pagination.Item>
    );
  }

  const handleChangeRole = async (currentUser: User) => {
    if (!currentUser || !currentUser.id || !user.username) {
      return;
    }

    try {
      const res = await changeUserRole(currentUser.id, user.username);
      setLoading(true);
      console.log(res);
    } catch (err: any) {
      setError(err.request.responseText);
    }
  };

  const handleDeleteAccount = async (id: number | undefined) => {
    if (!id) {
      return;
    }

    try {
      const res = await deleteUserAccount(id);
      setLoading(true);
      console.log(res);
    } catch (err: any) {
      setError(err.request.responseText);
    }
  };

  const handleAcceptRequest = async (username: string | undefined) => {
    if (!username) {
      return;
    }

    try {
      const res = await acceptUserRequest(username);
      setLoading(true);
      console.log(res);
    } catch (err: any) {
      setError(err.request.responseText);
    }
  };

  const handleRejectRequest = async (username: string | undefined) => {
    if (!username) {
      return;
    }

    try {
      const res = await rejectUserRequest(username);
      setLoading(true);
      console.log(res);
    } catch (err: any) {
      setError(err.request.responseText);
    }
  };

  const paginationStyle = {
    marginTop: "5%",
  };

  if (loading) {
    return <Spinner animation='border' variant='primary' />;
  } else {
    return (
      <div>
        <Nav username={user.username} setAuth={setAuth} isAdmin={true} user={user} />
        <Container>
          <Form className='my-4'>
            <Form.Check
              type='switch'
              id='pending'
              label='Pending Requests'
              defaultChecked={pending}
              onClick={() => setPending(!pending)}
            />
          </Form>
          {loading ? (
            <Spinner animation='border' variant='primary' />
          ) : pending ? (
            <>
              <h1 className='display-4 mb-5'>Pending Requests</h1>
              <Card>
                <ListGroup variant='flush'>
                  {currentRequests.map((user, i) => (
                    <ListGroup.Item key={i}>
                      <strong>{user.username}</strong>
                      <br />
                      Email: {user.email}
                      <br />
                      <Button
                        variant='primary'
                        className='mt-2 me-3'
                        onClick={() => handleAcceptRequest(user.username)}
                      >
                        Accept
                      </Button>
                      <Button
                        variant='danger'
                        className='mt-2'
                        onClick={() => handleRejectRequest(user.username)}
                      >
                        Reject
                      </Button>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              </Card>
              {pendingRequests.length > 0 && (
                <Pagination className='justify-content-center' style={paginationStyle}>
                  <Pagination.First onClick={() => setCurrentPage(1)} />
                  <Pagination.Prev
                    onClick={() =>
                      currentPage != 1 ? setCurrentPage(currentPage - 1) : setCurrentPage(1)
                    }
                  />
                  {paginationItems.slice(
                    currentPage > 3 ? currentPage - 3 : 0,
                    currentPage < totalPages - 3 ? currentPage + 2 : totalPages
                  )}
                  <Pagination.Next
                    onClick={() =>
                      currentPage != totalPages
                        ? setCurrentPage(currentPage + 1)
                        : setCurrentPage(totalPages)
                    }
                  />
                  <Pagination.Last onClick={() => setCurrentPage(totalPages)} />
                </Pagination>
              )}
            </>
          ) : (
            <>
              <h1 className='display-4 mb-5'>Users</h1>
              <Card>
                <ListGroup variant='flush'>
                  {currentUsers.map((user, i) => (
                    <ListGroup.Item key={i}>
                      <strong>{user.username}</strong>
                      <br />
                      Email: {user.email}
                      <br />
                      Role: {user.role}
                      <div className='my-3'>
                        <Button
                          variant='primary'
                          onClick={() => handleChangeRole(user)}
                          style={{ marginRight: "10px" }}
                        >
                          Change Role
                        </Button>

                        <Button variant='danger' onClick={() => handleDeleteAccount(user.id)}>
                          Delete Account
                        </Button>
                      </div>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              </Card>
              {users.length > 0 && (
                <Pagination className='justify-content-center' style={paginationStyle}>
                  <Pagination.First onClick={() => setCurrentPage(1)} />
                  <Pagination.Prev
                    onClick={() =>
                      currentPage != 1 ? setCurrentPage(currentPage - 1) : setCurrentPage(1)
                    }
                  />
                  {paginationItems.slice(
                    currentPage > 3 ? currentPage - 3 : 0,
                    currentPage < totalPages - 3 ? currentPage + 2 : totalPages
                  )}
                  <Pagination.Next
                    onClick={() =>
                      currentPage != totalPages
                        ? setCurrentPage(currentPage + 1)
                        : setCurrentPage(totalPages)
                    }
                  />
                  <Pagination.Last onClick={() => setCurrentPage(totalPages)} />
                </Pagination>
              )}
            </>
          )}
        </Container>
        <Footer />
        <ErrorToast title='Error' msg={error} show={show} setShow={setShow} />
      </div>
    );
  }
};

export default AdminDashboard;
