import { useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Button,
  Form,
  Image,
  DropdownButton,
  Dropdown,
  Spinner,
} from "react-bootstrap";
import { useForm, SubmitHandler } from "react-hook-form";
import { User, UserData, UserProfileProps } from "../types";
import {
  addFriendRequest,
  getFriendRequests,
  getFriends,
  getUserData,
  postUserData,
  deleteFriend,
} from "../api/userApi";
import { useNavigate, useParams } from "react-router";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHouse } from "@fortawesome/free-solid-svg-icons/faHouse";
import Select, { MultiValue } from "react-select";
import ErrorAlert from "./ErrorAlert";
import { INTERESTS } from "../constants";

/**
 * `UserProfile` renders the user profile page. It allows for viewing and editing
 * a user's profile, including their interests, status, and personal information.
 *
 * @component
 * @param {UserProfileProps} props - The props for the UserProfile component.
 * @param {string} props.currentUsername - The username of the currently logged-in user.
 *
 * @returns {React.ReactElement} The UserProfile component.
 *
 * @example
 * ```tsx
 * <UserProfile currentUsername="johndoe" />
 * ```
 *
 * ### API Functions Used
 * - `getFriends` to fetch the user's friends.
 * - `getUserData` to fetch the user's data.
 * - `postUserData` to update the user's data.
 * - `deleteFriend` to remove a friend from the user's friend list.
 *
 * ### External Libraries and Components Used
 * - `react-router` for routing and navigation.
 * - `@fortawesome/react-fontawesome` for icons.
 * - `react-select` for the interests multi-select input.
 * - `ErrorAlert` for displaying error messages.
 *
 * ### State Management
 * - Uses local state for managing editing mode, user interests, loading state, friendship status, and more.
 * - Utilizes `useForm` from `react-hook-form` for form handling.
 *
 * ### Side Effects
 * - On component mount, fetches the user's data and updates the component state accordingly.
 */
const UserProfile = ({ currentUsername }: UserProfileProps): React.ReactElement => {
  const [editing, setEditing] = useState(false);
  const [interests, setInterests] = useState<string[]>([]);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<User>();
  const [status, setStatus] = useState<string | undefined>("Available");
  const [loading, setLoading] = useState(true);
  const [triggerFetch, setTriggerFetch] = useState(false);
  const [isFriend, setIsFriend] = useState(false);
  const [pendingRequest, setPendingRequest] = useState(false);
  const navigate = useNavigate();
  const { username } = useParams();
  const [user, setUser] = useState<User>({
    username: username,
    email: undefined,
    password: undefined,
    bio: undefined,
    role: undefined,
    photo: undefined,
    interests: interests,
    status: undefined,
    friends: [],
    friendRequests: [],
  });

  useEffect(() => {
    /**
     * Asynchronously fetches and updates the user profile data.
     *
     * This function performs the following operations:
     * 1. Initiates loading state to indicate the start of data fetching.
     * 2. Retrieves detailed user data, the list of friends, and friend requests for the current user.
     * 3. Updates the component state with the fetched data, including user details, status, interests, and friends.
     * 4. Determines if the current user is a friend or has a pending friend request with the profile being viewed.
     * 5. Resets the loading state upon completion of data fetching.
     *
     * It is triggered upon component mount and whenever there's a change in the user's username, the triggerFetch state,
     * the length of the user's friend requests, or the currentUsername state.
     */
    const fetchData = async () => {
      setLoading(true);
      const data = await getUserData(user.username);
      const friends = await getFriends(user.username);
      const friendRequests = await getFriendRequests(user.username);
      const currentUserFriendRequests = await getFriendRequests(currentUsername);

      setUser({
        ...data,
        username: user.username,
        friends: friends,
        friendRequests: friendRequests,
      });
      setStatus(data.status);
      setInterests(data.interests || []);

      let userExists: User | undefined = undefined;

      // check if current user is in the friends list of the user whose profile is being viewed
      if (friends) {
        userExists = friends.find((u: User) => u.username === currentUsername);
        setIsFriend(userExists !== undefined);
      }

      // check if the current user is in the pending request list of the user whose profile is being viewed
      if (friendRequests) {
        userExists = friendRequests.find((u: User) => u.username === currentUsername);
        setPendingRequest(userExists !== undefined);
      }

      // check if the user whose profile is being viewed in the is in the pending request list of the current user
      // we only need to check if the user is not already friends, or they are not in the pending request list.
      if (currentUserFriendRequests && !userExists) {
        userExists = currentUserFriendRequests.find((u: User) => u.username === user.username);
        setPendingRequest(userExists !== undefined);
      }

      setLoading(false);
    };

    fetchData();
  }, [user.username, triggerFetch, currentUsername]);

  const onSubmit: SubmitHandler<User> = async data => {
    if (user.username === undefined || currentUsername !== user.username) {
      return;
    }

    const userObj: UserData = {
      username: user.username,
      bio: data.bio,
      status: status,
      photo: data.photo,
      interests: interests,
    };

    const res = await postUserData(userObj, user.username);
    console.log(res);
    setEditing(false);
    setTriggerFetch(!triggerFetch);
  };

  const onAddFriend = async () => {
    await addFriendRequest(currentUsername, user.username);
    setTriggerFetch(!triggerFetch);
  };

  const onDeleteFriend = async () => {
    await deleteFriend(currentUsername, user.username);
    setTriggerFetch(!triggerFetch);
  };

  const handleInterestChange = async (
    selectedOptions: MultiValue<{ value: string; label: string }>
  ) => {
    const selectedValues = selectedOptions.map(option => option.value);
    setInterests(selectedValues);
  };

  if (loading) {
    return <Spinner animation='border' variant='primary' />;
  } else if (user.email === undefined) {
    return <ErrorAlert />;
  } else {
    return (
      <Container className='mt-5'>
        <Button variant='outline-secondary' onClick={() => navigate("/")} className='mb-3'>
          <FontAwesomeIcon icon={faHouse} />
        </Button>
        <Row className='justify-content-md-center'>
          <Col md={6}>
            <div className='text-center'>
              <Image
                src={user.photo ? user.photo : import.meta.env.VITE_LOGO_PATH}
                alt='Profile Picture'
                roundedCircle
                thumbnail
                style={{ width: 150, height: 150 }}
              />
              <h2 className='mt-3 display-3'>{user.username}</h2>
            </div>
            {!editing ? (
              <>
                <Row>
                  <Button
                    disabled
                    style={{
                      backgroundColor:
                        status === "Available"
                          ? "green"
                          : status === "Busy"
                          ? "red"
                          : status === "Last seen recently"
                          ? "blue"
                          : status === "Away"
                          ? "gray"
                          : "black",
                      borderColor:
                        status === "Available"
                          ? "green"
                          : status === "Busy"
                          ? "red"
                          : status === "Last seen recently"
                          ? "blue"
                          : status === "Away"
                          ? "gray"
                          : "black",
                    }}
                  >
                    {status}
                  </Button>
                </Row>
                <div className='mt-4'>
                  <h3>Bio</h3>
                  <hr />
                  <p>{user.bio}</p>
                </div>
                <div className='mt-4'>
                  <h3>Interests</h3>
                  <hr />
                  {interests.length > 0 ? (
                    <div className='d-flex flex-wrap'>
                      {interests.map((interest, i) => (
                        <span key={i} className='badge bg-secondary me-2 mb-2'>
                          {interest}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <p>No interests added yet.</p>
                  )}
                </div>
                {currentUsername === user.username ? (
                  <Button className='mt-3' variant='primary' onClick={() => setEditing(true)}>
                    Edit Profile
                  </Button>
                ) : (
                  <>
                    {isFriend ? (
                      <Button className='mt-3' variant='primary' onClick={onDeleteFriend}>
                        Remove Friend
                      </Button>
                    ) : pendingRequest ? (
                      <Button disabled className='mt-3' variant='primary'>
                        Sent Request
                      </Button>
                    ) : (
                      <Button className='mt-3' variant='primary' onClick={onAddFriend}>
                        Add Friend
                      </Button>
                    )}
                  </>
                )}
              </>
            ) : (
              <>
                <Row>
                  <DropdownButton
                    id='userStatus'
                    title={status}
                    className='mt-3'
                    defaultValue={user.status}
                  >
                    <Dropdown.Item onClick={() => setStatus("Available")}>Available</Dropdown.Item>
                    <Dropdown.Item onClick={() => setStatus("Busy")}>Busy</Dropdown.Item>
                    <Dropdown.Item onClick={() => setStatus("Last seen recently")}>
                      Last seen recently
                    </Dropdown.Item>
                    <Dropdown.Item onClick={() => setStatus("Away")}>Away</Dropdown.Item>
                  </DropdownButton>
                </Row>
                <Form onSubmit={handleSubmit(onSubmit)} className='mt-4'>
                  <Form.Group controlId='bio' className='mt-3'>
                    <Form.Label>Bio</Form.Label>
                    <Form.Control as='textarea' defaultValue={user.bio} {...register("bio")} />
                    {errors.bio && (
                      <>
                        <Form.Text className='text-danger mb-2'>Bio is required</Form.Text>
                        <br />
                      </>
                    )}
                    <Form.Label>Interests</Form.Label>
                    <Select
                      isMulti
                      defaultValue={INTERESTS.filter(interest =>
                        interests.includes(interest.value)
                      )}
                      name='interests'
                      options={INTERESTS}
                      className='basic-multi-select'
                      classNamePrefix='select'
                      onChange={handleInterestChange}
                    />
                  </Form.Group>

                  <Form.Group controlId='photo' className='mt-3'>
                    <Form.Label>Photo url</Form.Label>
                    <Form.Control type='text' defaultValue={user.photo} {...register("photo")} />
                    {errors.photo && (
                      <Form.Text className='text-danger'>Photo is required</Form.Text>
                    )}
                  </Form.Group>
                  <Button type='submit' variant='success' className='my-4 me-2'>
                    Save
                  </Button>
                  <Button
                    variant='secondary'
                    className='my-4 ml-2'
                    onClick={() => setEditing(false)}
                  >
                    Cancel
                  </Button>
                </Form>
              </>
            )}
          </Col>
        </Row>
      </Container>
    );
  }
};

export default UserProfile;
