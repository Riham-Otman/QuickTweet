import { faUser } from "@fortawesome/free-regular-svg-icons";
import { faRightFromBracket, faSearch, faUserGroup } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { Col, Dropdown, Row } from "react-bootstrap";
import Container from "react-bootstrap/Container";
import Image from "react-bootstrap/Image";
import Navbar from "react-bootstrap/Navbar";
import { Link, useNavigate } from "react-router-dom";
import { updateStatus } from "../api/auth";
import { addFriend, getFriendRequests, searchUsers } from "../api/userApi";
import { INTERESTS } from "../constants";
import { NavBarProps, SearchResult, User } from "../types";
import AsyncSelect from "react-select/async";
import { components, SingleValue } from "react-select";

/**
 * Represents the navigation bar component of the application.
 *
 * This component displays the main navigation bar at the top of the application, providing links to different sections
 * of the application and displaying user-specific information such as friend requests. It supports functionalities like
 * searching, viewing friend requests, and logging out.
 *
 * The navigation bar is responsive and integrates with React Bootstrap for styling. It uses FontAwesome icons for visual
 * elements and interacts with the application's authentication and user APIs to fetch friend requests and handle user logout.
 *
 * @component
 * @param {NavBarProps} props - The properties passed to the Nav component.
 * @param {string} props.username - The username of the currently logged-in user.
 * @param {Dispatch<SetStateAction<boolean>>} props.setAuth - Function to update the authentication state of the application.
 * @param {boolean} props.isAdmin - Indicates if the currently logged-in user has administrative privileges.
 */
const Nav = ({ username, setAuth, isAdmin }: NavBarProps) => {
  const navigate = useNavigate();
  const [friendRequests, setFriendRequests] = useState<User[]>([]);

  const fetchRequests = async () => {
    try {
      const requests = await getFriendRequests(username);
      setFriendRequests(requests);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const getSearchResults = async (query: string) => {
    if (query.length === 0) {
      return;
    }

    const results = await searchUsers(query);

    // map the results to the format expected by the search component
    // each element should have a value and a path to the element's page
    let combinedResults = results.map(user => ({
      value: user.username || "",
      label: user.username || "",
      path: `/profile/${user.username}`,
    }));
    combinedResults = combinedResults.concat(
      INTERESTS.filter(interest => interest.value.toLowerCase().includes(query.toLowerCase())).map(
        interest => ({
          value: interest.value,
          label: interest.value,
          path: `/groups/${interest.value}`,
        })
      )
    );

    return combinedResults;
  };

  const DropdownIndicator = (props: any) => {
    return (
      components.DropdownIndicator && (
        <components.DropdownIndicator {...props}>
          <FontAwesomeIcon icon={faSearch} />
        </components.DropdownIndicator>
      )
    );
  };

  const handleLogout = async () => {
    try {
      await updateStatus(username, "Offline");
      sessionStorage.removeItem("jwt");
      sessionStorage.removeItem("user");
      setAuth(false);
      navigate("/");
    } catch (err) {
      console.error(err);
    }
  };

  const onAddFriend = async (friendUsername: string | null) => {
    if (friendUsername == null) {
      return;
    }

    const msg = await addFriend(username, friendUsername);
    console.log(msg);
    fetchRequests();
  };

  return (
    <Navbar bg='light' data-bs-theme='light'>
      <Container
        fluid
        className='align-items-center justify-content-around'
        style={{ width: "90%" }}
      >
        <Col>
          <Navbar.Brand className='ms-5'>
            <Link to='/'>
              <Image
                src={import.meta.env.VITE_LOGO_PATH}
                width='60'
                height='60'
                className='d-inline-block align-top'
              />
            </Link>
          </Navbar.Brand>
        </Col>

        <Col>
          <div className='w-50 my-3' style={{ position: "relative", zIndex: 1000 }}>
            <AsyncSelect
              cacheOptions
              defaultOptions
              components={{ DropdownIndicator }}
              onChange={(selectedOption: SingleValue<SearchResult>) => {
                if (selectedOption !== null) {
                  navigate(selectedOption.path);
                }
              }}
              loadOptions={(query, callback) => {
                getSearchResults(query).then(options => {
                  callback(options || []);
                });
              }}
            />
          </div>
        </Col>

        <Row className='align-items-center'>
          {isAdmin && (
            <Col>
              <Link to='/admin' style={{ textDecoration: "none" }}>
                Admin
              </Link>
            </Col>
          )}

          <Col>
            <Dropdown onSelect={onAddFriend}>
              <Dropdown.Toggle variant='outline-primary' id='navDropdown'>
                <FontAwesomeIcon icon={faUserGroup} />
              </Dropdown.Toggle>
              <Dropdown.Menu>
                {friendRequests && friendRequests.length > 0 ? (
                  <>
                    {friendRequests.map(request => (
                      <Dropdown.Item
                        key={request.username}
                        eventKey={request.username}
                        className='mb-3'
                      >
                        <Image src={request.photo} className='w-25 me-1' />
                        <span>{request.username}</span>
                        <span className='ms-3 me-5 w-50 border p-2 bg-primary text-white'>
                          Accept
                        </span>
                      </Dropdown.Item>
                    ))}
                  </>
                ) : (
                  <Dropdown.Item disabled>No Requests</Dropdown.Item>
                )}
              </Dropdown.Menu>
            </Dropdown>
          </Col>
          <Col className='me-5'>
            <Dropdown>
              <Dropdown.Toggle variant='success' id='navDropdown'>
                {username}
              </Dropdown.Toggle>

              <Dropdown.Menu>
                <Dropdown.Item onClick={() => navigate(`/profile/${username}`)}>
                  <FontAwesomeIcon icon={faUser} /> Profile
                </Dropdown.Item>
                <Dropdown.Item onClick={handleLogout}>
                  <FontAwesomeIcon icon={faRightFromBracket} /> Logout
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          </Col>
        </Row>
      </Container>
    </Navbar>
  );
};

export default Nav;
